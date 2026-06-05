// ============================================================
//  api.js – cliente HTTP centralizado
//  Todas las páginas importan este módulo
// ============================================================

const BASE = '';  


function getToken() {
  return localStorage.getItem('token');
}

async function request(method, path, body = null) {
  const headers = { 'Content-Type': 'application/json' };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(BASE + path, opts);

  if (res.status === 401) {
    // Token expirado o inválido → redirigir a login
    localStorage.clear();
    window.location.href = '/login.html';
    return;
  }

  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (!res.ok) {
    const msg = data?.message || data?.mensaje || data?.error || 'Error desconocido';
    throw new Error(msg);
  }

  return data;
}

const api = {
  // Auth
  login:    (correo, password)     => request('POST', '/api/auth/login',    { correo, password }),
  registro: (nombre, correo, password) => request('POST', '/api/auth/registro', { nombre, correo, password }),

  // Usuario autenticado
  getMe:           ()      => request('GET',    '/api/usuarios/me'),
  updateMe:        (data)  => request('PUT',    '/api/usuarios/me', data),
  changePassword:  (data)  => request('PUT',    '/api/usuarios/me/password', data),
  deleteMe:        ()      => request('DELETE', '/api/usuarios/me'),

  // Admin
  getAllUsers:     ()       => request('GET',    '/api/admin/usuarios'),
  createUser:     (data)   => request('POST',   '/api/admin/usuarios', data),
  updateUser:     (id, d)  => request('PUT',    `/api/admin/usuarios/${id}`, d),
  deleteUser:     (id)     => request('DELETE', `/api/admin/usuarios/${id}`),
};

window.api = api;
