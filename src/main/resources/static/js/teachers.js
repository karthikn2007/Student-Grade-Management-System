/**
 * Teacher / Faculty Management Module
 */
const Teachers = {
  teachersList: [],
  teacherModal: null,

  init() {
    const modalEl = document.getElementById('teacherModal');
    if (modalEl) this.teacherModal = new bootstrap.Modal(modalEl);

    const form = document.getElementById('teacherForm');
    if (form) {
      form.addEventListener('submit', (e) => this.handleSave(e));
    }

    const addBtn = document.getElementById('addTeacherBtn');
    if (addBtn) {
      addBtn.addEventListener('click', () => this.openAddModal());
    }

    const searchInput = document.getElementById('teacherSearchInput');
    if (searchInput) {
      searchInput.addEventListener('input', () => this.filterTeachers());
    }
  },

  async loadTeachers() {
    try {
      const data = await API.get('/teachers');
      this.teachersList = data || [];
      this.renderTable(this.teachersList);
    } catch (err) {
      App.showToast('Failed to load faculty: ' + err.message, 'danger');
    }
  },

  filterTeachers() {
    const searchVal = (document.getElementById('teacherSearchInput')?.value || '').toLowerCase().trim();
    const filtered = this.teachersList.filter(t =>
      !searchVal ||
      t.fullName.toLowerCase().includes(searchVal) ||
      t.employeeId.toLowerCase().includes(searchVal) ||
      t.department.toLowerCase().includes(searchVal) ||
      t.email.toLowerCase().includes(searchVal)
    );
    this.renderTable(filtered);
  },

  renderTable(teachers) {
    const tbody = document.getElementById('teachersTableBody');
    if (!tbody) return;

    if (!teachers || teachers.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="text-center py-4 text-muted">
            <i class="bi bi-person-badge fs-2 d-block mb-2 text-secondary"></i>
            No faculty members found.
          </td>
        </tr>
      `;
      return;
    }

    const isAdmin = Auth.isAdmin();

    tbody.innerHTML = teachers.map((t, idx) => `
      <tr>
        <td class="fw-semibold text-muted">${idx + 1}</td>
        <td>
          <span class="badge bg-light text-primary border fw-bold">${t.employeeId}</span>
        </td>
        <td>
          <div class="fw-bold text-dark">${t.fullName}</div>
          <small class="text-muted">${t.designation}</small>
        </td>
        <td><span class="badge bg-secondary-subtle text-secondary">${t.department}</span></td>
        <td>${t.email}</td>
        <td>${t.phoneNumber || 'N/A'}</td>
        <td class="text-end">
          ${isAdmin ? `
            <button class="btn-action admin-only" title="Edit Faculty" onclick="Teachers.openEditModal(${t.id})">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn-action btn-action-delete admin-only" title="Delete Faculty" onclick="Teachers.deleteTeacher(${t.id}, '${t.fullName}')">
              <i class="bi bi-trash"></i>
            </button>
          ` : '<span class="text-muted small">View Only</span>'}
        </td>
      </tr>
    `).join('');
  },

  openAddModal() {
    document.getElementById('teacherModalTitle').textContent = 'Add Faculty Member';
    document.getElementById('teacherForm').reset();
    document.getElementById('teacherId').value = '';
    this.teacherModal.show();
  },

  async openEditModal(id) {
    try {
      const teacher = await API.get(`/teachers/${id}`);
      document.getElementById('teacherModalTitle').textContent = 'Update Faculty Details';
      document.getElementById('teacherId').value = teacher.id;
      document.getElementById('teacherEmpId').value = teacher.employeeId;
      document.getElementById('teacherName').value = teacher.fullName;
      document.getElementById('teacherEmail').value = teacher.email;
      document.getElementById('teacherPhone').value = teacher.phoneNumber || '';
      document.getElementById('teacherDept').value = teacher.department;
      document.getElementById('teacherDesignation').value = teacher.designation;

      this.teacherModal.show();
    } catch (err) {
      App.showToast('Failed to fetch faculty details: ' + err.message, 'danger');
    }
  },

  async handleSave(e) {
    e.preventDefault();
    const id = document.getElementById('teacherId').value;

    const payload = {
      employeeId: document.getElementById('teacherEmpId').value.trim(),
      fullName: document.getElementById('teacherName').value.trim(),
      email: document.getElementById('teacherEmail').value.trim(),
      phoneNumber: document.getElementById('teacherPhone').value.trim(),
      department: document.getElementById('teacherDept').value.trim(),
      designation: document.getElementById('teacherDesignation').value.trim()
    };

    try {
      if (id) {
        await API.put(`/teachers/${id}`, payload);
        App.showToast('Faculty updated successfully!', 'success');
      } else {
        await API.post('/teachers', payload);
        App.showToast('Faculty member added successfully!', 'success');
      }

      this.teacherModal.hide();
      await this.loadTeachers();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast(err.message, 'danger');
    }
  },

  async deleteTeacher(id, name) {
    if (!confirm(`Are you sure you want to remove faculty member "${name}"?`)) {
      return;
    }

    try {
      await API.delete(`/teachers/${id}`);
      App.showToast(`Faculty member "${name}" removed successfully.`, 'info');
      await this.loadTeachers();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast('Failed to delete faculty member: ' + err.message, 'danger');
    }
  }
};
