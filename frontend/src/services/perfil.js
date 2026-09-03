import http from './http'

export async function obtenerPerfil(idUsuario) {
  const { data } = await http.get(`/users/profile/${idUsuario}`)
  return data
}
