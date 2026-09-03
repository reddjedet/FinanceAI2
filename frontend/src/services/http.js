import axios from 'axios'

const CLAVE_TOKEN = 'financeai:token'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'

const http = axios.create({
  baseURL: apiBaseUrl,
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor: agrega Bearer token a cada request si existe
http.interceptors.request.use((config) => {
  const token = localStorage.getItem(CLAVE_TOKEN)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Interceptor de respuesta: si el backend devuelve 401 (token ausente o expirado)
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const url = error.config?.url || ''

    if (status === 401 && !url.includes('/auth/login')) {
      localStorage.removeItem(CLAVE_TOKEN)
      localStorage.removeItem('financeai:sessid')
      localStorage.removeItem('financeai:nombre')
      localStorage.removeItem('financeai:ingreso-original')

      if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export default http
