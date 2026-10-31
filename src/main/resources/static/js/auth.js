/**
 * Authentication & Role Authorization Module
 */
const Auth = {
  currentUser: null,
  loginModalInstance: null,

  init() {
    const modalEl = document.getElementById('loginModal');
    if (modalEl) {
      this.loginModalInstance = new bootstrap.Modal(modalEl, {
        backdrop: 'static',
        keyboard: false
      });
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
      loginForm.addEventListener('submit', (e) => this.handleLogin(e));
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => this.handleLogout());
    }

    // Quick demo login fill buttons
    const fillAdminBtn = document.getElementById('fillAdminLogin');
    if (fillAdminBtn) {
      fillAdminBtn.addEventListener('click', () => {
        document.getElementById('loginUsername').value = 'admin';
        document.getElementById('loginPassword').value = 'admin123';
      });
    }

    const fillTeacherBtn = document.getElementById('fillTeacherLogin');
    if (fillTeacherBtn) {
      fillTeacherBtn.addEventListener('click', () => {
        document.getElementById('loginUsername').value = 'prof.smith';
        document.getElementById('loginPassword').value = 'teacher123';
      });
    }

    this.checkSession();
  },

  async checkSession() {
    try {
      const user = await API.get('/auth/me');
      if (user && user.id) {
        this.currentUser = user;
        this.updateUserUI();
        if (this.loginModalInstance) {
          this.loginModalInstance.hide();
        }
        App.loadActiveView();
      } else {
        this.showLoginModal();
      }
    } catch (e) {
      this.showLoginModal();
    }
  },

  showLoginModal() {
    if (this.loginModalInstance) {
      this.loginModalInstance.show();
    }
  },

  async handleLogin(e) {
    e.preventDefault();
    const usernameInput = document.getElementById('loginUsername');
    const passwordInput = document.getElementById('loginPassword');
    const loginError = document.getElementById('loginError');

    loginError.classList.add('d-none');
    loginError.textContent = '';

    const payload = {
      username: usernameInput.value.trim(),
      password: passwordInput.value
    };

    try {
      const res = await API.post('/auth/login', payload);
      this.currentUser = {
        id: res.id,
        username: res.username,
        fullName: res.fullName,
        email: res.email,
        role: res.role
      };

      this.updateUserUI();
      if (this.loginModalInstance) {
        this.loginModalInstance.hide();
      }

      App.showToast(`Welcome back, ${res.fullName}!`, 'success');
      App.loadActiveView();
    } catch (err) {
      loginError.textContent = err.message || 'Invalid username or password';
      loginError.classList.remove('d-none');
    }
  },

  async handleLogout() {
    try {
      await API.post('/auth/logout', {});
    } catch (err) {
      console.warn('Logout warning:', err);
    } finally {
      this.currentUser = null;
      this.updateUserUI();
      App.showToast('You have been logged out.', 'info');
      this.showLoginModal();
    }
  },

  updateUserUI() {
    const userNameEl = document.getElementById('sidebarUserName');
    const userRoleEl = document.getElementById('sidebarUserRole');
    const userAvatarEl = document.getElementById('sidebarUserAvatar');
    const topbarUserBadge = document.getElementById('topbarUserBadge');

    if (this.currentUser) {
      if (userNameEl) userNameEl.textContent = this.currentUser.fullName || this.currentUser.username;
      if (userRoleEl) userRoleEl.textContent = this.currentUser.role;
      if (userAvatarEl) {
        const initials = (this.currentUser.fullName || this.currentUser.username).slice(0, 2).toUpperCase();
        userAvatarEl.textContent = initials;
      }
      if (topbarUserBadge) {
        topbarUserBadge.textContent = `${this.currentUser.role}: ${this.currentUser.fullName}`;
      }

      // Role gating: Hide or disable admin-only actions for teachers
      const adminOnlyElements = document.querySelectorAll('.admin-only');
      adminOnlyElements.forEach(el => {
        if (this.currentUser.role === 'ADMIN') {
          el.classList.remove('d-none');
          el.removeAttribute('disabled');
        } else {
          el.classList.add('d-none');
          el.setAttribute('disabled', 'true');
        }
      });
    } else {
      if (userNameEl) userNameEl.textContent = 'Guest';
      if (userRoleEl) userRoleEl.textContent = 'Not logged in';
      if (userAvatarEl) userAvatarEl.textContent = 'G';
      if (topbarUserBadge) topbarUserBadge.textContent = 'Not logged in';
    }
  },

  isAdmin() {
    return this.currentUser && this.currentUser.role === 'ADMIN';
  }
};
