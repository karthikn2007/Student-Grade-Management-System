/**
 * API Client Module - Student Grade Management System
 * Wrapper around standard Fetch API for clean REST communication.
 */
const API = {
  baseUrl: '/api',

  async request(endpoint, options = {}) {
    const defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    const config = {
      ...options,
      credentials: 'same-origin',
      headers: {
        ...defaultHeaders,
        ...(options.headers || {})
      }
    };

    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, config);

      if (response.status === 401) {
        // Session expired or unauthenticated
        if (!endpoint.includes('/auth/login') && !endpoint.includes('/auth/me')) {
          App.showToast('Session expired. Please log in again.', 'warning');
          Auth.showLoginModal();
        }
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Unauthorized');
      }

      if (response.status === 403) {
        App.showToast('Access denied. Administrator privileges required.', 'danger');
        throw new Error('Access denied (403)');
      }

      if (response.status === 204) {
        return null;
      }

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        let errorMsg = (data && data.message) ? data.message : `Request failed (${response.status})`;
        if (data && data.details && Array.isArray(data.details)) {
          errorMsg += ': ' + data.details.join(', ');
        }
        throw new Error(errorMsg);
      }

      return data;
    } catch (err) {
      console.error(`API Error [${endpoint}]:`, err);
      throw err;
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  },

  post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body)
    });
  },

  put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body)
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
};
