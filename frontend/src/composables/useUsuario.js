import { useUsuarioStore } from '@/stores/usuario'
import { useAuthStore } from '@/stores/auth'
import { useAnalisisFinancieroStore } from '@/stores/analisisFinanciero'
import { registrarUsuario, obtenerUsuario, loginUsuario, actualizarSueldo, obtenerResumenMensual, eliminarCuenta } from '@/services/usuarios'
import { useTransacciones } from '@/composables/useTransacciones'
import { mensajeErrorApi } from '@/utils/errores'
import { datosDemo } from '@/data/demo'

export function useUsuario() {
  const store = useUsuarioStore()
  const auth = useAuthStore()
  const analisisStore = useAnalisisFinancieroStore()
  const { listarTransacciones } = useTransacciones()

  async function cargarUsuario(id) {
    if (!id && id !== 0) return null
    store.setCargando(true)
    store.setError('')
    try {
      const usuario = await obtenerUsuario(id)
      store.setUsuario({
        id: usuario.id,
        nombre: usuario.nombre || store.nombre,
        ingresoDisponible: usuario.ingresoMensual,
        ingresoOriginal: usuario.ingresoMensual,
      })
      
      if (id !== null && id !== 0) {
        try {
          const resumenes = await obtenerResumenMensual(id)
          store.setResumenesMensuales(resumenes)
        } catch (e) {
          console.error("Error al obtener los resumenes mensuales:", e)
        }
      }

      await listarTransacciones()
      return usuario
    } catch (error) {
      store.setError(mensajeErrorApi(error))
      throw new Error(mensajeErrorApi(error), { cause: error })
    } finally {
      store.setCargando(false)
    }
  }

  async function registrarYEntrar(datos) {
    store.setCargando(true)
    store.setError('')
    analisisStore.reset()
    localStorage.removeItem('financeai:resumenes-gastos')
    try {
      await registrarUsuario(datos)
      const loginResp = await loginUsuario(datos.email, datos.password)
      const uid = loginResp.idUsuario ?? loginResp.id

      auth.iniciarSesion(uid, loginResp.token)
      await cargarUsuario(uid)
      
      const nombreUsuario = loginResp.nombre || datos.nombre
      store.setUsuario({
        id: uid,
        nombre: nombreUsuario,
        ingresoOriginal: datos.ingresoMensual,
      })
      if (nombreUsuario) {
        localStorage.setItem('financeai:nombre', nombreUsuario)
      }
      return uid
    } catch (error) {
      store.setError(mensajeErrorApi(error))
      throw new Error(mensajeErrorApi(error), { cause: error })
    } finally {
      store.setCargando(false)
    }
  }

  async function iniciarSesionCredenciales(email, password) {
    analisisStore.reset()
    localStorage.removeItem('financeai:resumenes-gastos')
    try {
      const loginResp = await loginUsuario(email, password)
      const uid = loginResp.idUsuario ?? loginResp.id
      
      auth.iniciarSesion(uid, loginResp.token)
      if (loginResp.nombre) {
        localStorage.setItem('financeai:nombre', loginResp.nombre)
      }
      return { id: uid, nombre: loginResp.nombre }
    } catch (error) {
      throw new Error(mensajeErrorApi(error), { cause: error })
    }
  }

  async function editarSueldo(nuevoSueldo) {
    store.setCargando(true)
    store.setError('')
    try {
      if (store.id) {
        await actualizarSueldo(store.id, nuevoSueldo)
      }
      store.setUsuario({
        id: store.id,
        ingresoOriginal: nuevoSueldo,
        ingresoDisponible: nuevoSueldo,
      })
    } catch (error) {
      store.setError(mensajeErrorApi(error))
      throw new Error(mensajeErrorApi(error), { cause: error })
    } finally {
      store.setCargando(false)
    }
  }

  async function desactivarCuenta() {
    if (!store.id) return
    store.setCargando(true)
    store.setError('')
    try {
      await eliminarCuenta(store.id)
      salir()
    } catch (error) {
      store.setError(mensajeErrorApi(error))
      throw new Error(mensajeErrorApi(error), { cause: error })
    } finally {
      store.setCargando(false)
    }
  }

  function salir() {
    store.limpiar()
    analisisStore.reset()
    auth.cerrarSesion()
    localStorage.removeItem('financeai:nombre')
    localStorage.removeItem('financeai:resumenes-gastos')
  }

  function entrarDemo() {
    store.setUsuario({
      id: null,
      nombre: datosDemo.nombre,
      ingresoDisponible: datosDemo.ingresoDisponible,
      ingresoOriginal: datosDemo.ingresoDisponible,
    })
    store.setTransacciones(datosDemo.transacciones)
  }

  return { cargarUsuario, registrarYEntrar, iniciarSesionCredenciales, editarSueldo, desactivarCuenta, salir, entrarDemo }
}
