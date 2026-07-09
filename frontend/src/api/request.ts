const API_BASE = 'http://127.0.0.1:8080'

export async function apiRequest(url: string, options?: RequestInit) {
  const response = await fetch(API_BASE + url, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...options,
  })

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }

  return await response.json()
}
