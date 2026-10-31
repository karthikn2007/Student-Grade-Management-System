/**
 * Subject Management Module
 */
const Subjects = {
  subjectsList: [],
  subjectModal: null,

  init() {
    const modalEl = document.getElementById('subjectModal');
    if (modalEl) this.subjectModal = new bootstrap.Modal(modalEl);

    const form = document.getElementById('subjectForm');
    if (form) {
      form.addEventListener('submit', (e) => this.handleSave(e));
    }

    const addBtn = document.getElementById('addSubjectBtn');
    if (addBtn) {
      addBtn.addEventListener('click', () => this.openAddModal());
    }

    const searchInput = document.getElementById('subjectSearchInput');
    if (searchInput) {
      searchInput.addEventListener('input', () => this.filterSubjects());
    }

    const deptFilter = document.getElementById('subjectDeptFilter');
    if (deptFilter) {
      deptFilter.addEventListener('change', () => this.filterSubjects());
    }

    const semFilter = document.getElementById('subjectSemFilter');
    if (semFilter) {
      semFilter.addEventListener('change', () => this.filterSubjects());
    }
  },

  async loadSubjects() {
    try {
      const data = await API.get('/subjects');
      this.subjectsList = data || [];
      this.populateFilters();
      this.renderTable(this.subjectsList);
    } catch (err) {
      App.showToast('Failed to load subjects: ' + err.message, 'danger');
    }
  },

  populateFilters() {
    const deptFilter = document.getElementById('subjectDeptFilter');
    const semFilter = document.getElementById('subjectSemFilter');

    if (deptFilter) {
      const depts = Array.from(new Set(this.subjectsList.map(s => s.department).filter(Boolean)));
      const currDept = deptFilter.value;
      deptFilter.innerHTML = '<option value="">All Departments</option>' +
        depts.map(d => `<option value="${d}">${d}</option>`).join('');
      deptFilter.value = currDept;
    }

    if (semFilter) {
      const sems = Array.from(new Set(this.subjectsList.map(s => s.semester).filter(Boolean)));
      const currSem = semFilter.value;
      semFilter.innerHTML = '<option value="">All Semesters</option>' +
        sems.map(s => `<option value="${s}">${s}</option>`).join('');
      semFilter.value = currSem;
    }
  },

  filterSubjects() {
    const searchVal = (document.getElementById('subjectSearchInput')?.value || '').toLowerCase().trim();
    const deptVal = document.getElementById('subjectDeptFilter')?.value || '';
    const semVal = document.getElementById('subjectSemFilter')?.value || '';

    const filtered = this.subjectsList.filter(s => {
      const matchesSearch = !searchVal ||
        s.subjectCode.toLowerCase().includes(searchVal) ||
        s.subjectName.toLowerCase().includes(searchVal);

      const matchesDept = !deptVal || s.department === deptVal;
      const matchesSem = !semVal || s.semester === semVal;

      return matchesSearch && matchesDept && matchesSem;
    });

    this.renderTable(filtered);
  },

  renderTable(subjects) {
    const tbody = document.getElementById('subjectsTableBody');
    if (!tbody) return;

    if (!subjects || subjects.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="text-center py-4 text-muted">
            <i class="bi bi-book fs-2 d-block mb-2 text-secondary"></i>
            No subjects found.
          </td>
        </tr>
      `;
      return;
    }

    const isAdmin = Auth.isAdmin();

    tbody.innerHTML = subjects.map((s, idx) => `
      <tr>
        <td class="fw-semibold text-muted">${idx + 1}</td>
        <td>
          <span class="badge bg-light text-primary border fw-bold">${s.subjectCode}</span>
        </td>
        <td><div class="fw-bold text-dark">${s.subjectName}</div></td>
        <td><span class="badge bg-secondary-subtle text-secondary">${s.department}</span></td>
        <td>${s.semester}</td>
        <td>
          <span class="fw-bold">${s.maxMarks}</span>
          <small class="text-muted ms-1">(Pass: ${s.passingMarks})</small>
        </td>
        <td class="text-end">
          ${isAdmin ? `
            <button class="btn-action admin-only" title="Edit Subject" onclick="Subjects.openEditModal(${s.id})">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn-action btn-action-delete admin-only" title="Delete Subject" onclick="Subjects.deleteSubject(${s.id}, '${s.subjectCode}')">
              <i class="bi bi-trash"></i>
            </button>
          ` : '<span class="text-muted small">View Only</span>'}
        </td>
      </tr>
    `).join('');
  },

  openAddModal() {
    document.getElementById('subjectModalTitle').textContent = 'Add New Subject';
    document.getElementById('subjectForm').reset();
    document.getElementById('subjectId').value = '';
    document.getElementById('subjectMaxMarks').value = 100;
    document.getElementById('subjectPassMarks').value = 40;
    this.subjectModal.show();
  },

  async openEditModal(id) {
    try {
      const subject = await API.get(`/subjects/${id}`);
      document.getElementById('subjectModalTitle').textContent = 'Update Subject';
      document.getElementById('subjectId').value = subject.id;
      document.getElementById('subjectCode').value = subject.subjectCode;
      document.getElementById('subjectName').value = subject.subjectName;
      document.getElementById('subjectDept').value = subject.department;
      document.getElementById('subjectSemester').value = subject.semester;
      document.getElementById('subjectMaxMarks').value = subject.maxMarks;
      document.getElementById('subjectPassMarks').value = subject.passingMarks;

      this.subjectModal.show();
    } catch (err) {
      App.showToast('Failed to fetch subject details: ' + err.message, 'danger');
    }
  },

  async handleSave(e) {
    e.preventDefault();
    const id = document.getElementById('subjectId').value;
    const maxMarks = parseFloat(document.getElementById('subjectMaxMarks').value);
    const passingMarks = parseFloat(document.getElementById('subjectPassMarks').value);

    if (passingMarks > maxMarks) {
      App.showToast('Passing marks cannot exceed maximum marks!', 'danger');
      return;
    }

    const payload = {
      subjectCode: document.getElementById('subjectCode').value.trim(),
      subjectName: document.getElementById('subjectName').value.trim(),
      department: document.getElementById('subjectDept').value.trim(),
      semester: document.getElementById('subjectSemester').value.trim(),
      maxMarks: maxMarks,
      passingMarks: passingMarks
    };

    try {
      if (id) {
        await API.put(`/subjects/${id}`, payload);
        App.showToast('Subject updated successfully!', 'success');
      } else {
        await API.post('/subjects', payload);
        App.showToast('Subject created successfully!', 'success');
      }

      this.subjectModal.hide();
      await this.loadSubjects();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast(err.message, 'danger');
    }
  },

  async deleteSubject(id, code) {
    if (!confirm(`Are you sure you want to delete subject "${code}"? All associated marks will be removed.`)) {
      return;
    }

    try {
      await API.delete(`/subjects/${id}`);
      App.showToast(`Subject "${code}" deleted successfully.`, 'info');
      await this.loadSubjects();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast('Failed to delete subject: ' + err.message, 'danger');
    }
  }
};
