/**
 * Student Management Module
 */
const Students = {
  studentsList: [],
  studentModal: null,
  profileModal: null,

  init() {
    const modalEl = document.getElementById('studentModal');
    if (modalEl) this.studentModal = new bootstrap.Modal(modalEl);

    const profModalEl = document.getElementById('studentProfileModal');
    if (profModalEl) this.profileModal = new bootstrap.Modal(profModalEl);

    const form = document.getElementById('studentForm');
    if (form) {
      form.addEventListener('submit', (e) => this.handleSave(e));
    }

    const searchInput = document.getElementById('studentSearchInput');
    if (searchInput) {
      searchInput.addEventListener('input', () => this.filterStudents());
    }

    const deptFilter = document.getElementById('studentDeptFilter');
    if (deptFilter) {
      deptFilter.addEventListener('change', () => this.filterStudents());
    }

    const addBtn = document.getElementById('addStudentBtn');
    if (addBtn) {
      addBtn.addEventListener('click', () => this.openAddModal());
    }
  },

  async loadStudents() {
    try {
      const data = await API.get('/students');
      this.studentsList = data || [];
      this.populateDepartmentFilter();
      this.renderTable(this.studentsList);
    } catch (err) {
      App.showToast('Failed to load students: ' + err.message, 'danger');
    }
  },

  populateDepartmentFilter() {
    const filter = document.getElementById('studentDeptFilter');
    if (!filter) return;

    const depts = Array.from(new Set(this.studentsList.map(s => s.department).filter(Boolean)));
    const currentVal = filter.value;

    filter.innerHTML = '<option value="">All Departments</option>' +
      depts.map(d => `<option value="${d}">${d}</option>`).join('');

    filter.value = currentVal;
  },

  filterStudents() {
    const searchVal = (document.getElementById('studentSearchInput')?.value || '').toLowerCase().trim();
    const deptVal = document.getElementById('studentDeptFilter')?.value || '';

    const filtered = this.studentsList.filter(s => {
      const matchesSearch = !searchVal ||
        s.fullName.toLowerCase().includes(searchVal) ||
        s.rollNumber.toLowerCase().includes(searchVal) ||
        s.email.toLowerCase().includes(searchVal);

      const matchesDept = !deptVal || s.department === deptVal;

      return matchesSearch && matchesDept;
    });

    this.renderTable(filtered);
  },

  renderTable(students) {
    const tbody = document.getElementById('studentsTableBody');
    if (!tbody) return;

    if (!students || students.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="8" class="text-center py-4 text-muted">
            <i class="bi bi-person-x fs-2 d-block mb-2 text-secondary"></i>
            No students found.
          </td>
        </tr>
      `;
      return;
    }

    const isAdmin = Auth.isAdmin();

    tbody.innerHTML = students.map((s, idx) => `
      <tr>
        <td class="fw-semibold text-muted">${idx + 1}</td>
        <td>
          <span class="badge bg-light text-primary border fw-bold">${s.rollNumber}</span>
        </td>
        <td>
          <div class="fw-bold text-dark">${s.fullName}</div>
          <small class="text-muted">${s.email}</small>
        </td>
        <td><span class="badge bg-secondary-subtle text-secondary">${s.department}</span></td>
        <td>${s.academicYear}</td>
        <td>${s.semester}</td>
        <td>${s.phoneNumber}</td>
        <td class="text-end">
          <button class="btn-action" title="View Profile" onclick="Students.viewProfile(${s.id})">
            <i class="bi bi-eye"></i>
          </button>
          ${isAdmin ? `
            <button class="btn-action admin-only" title="Edit Student" onclick="Students.openEditModal(${s.id})">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn-action btn-action-delete admin-only" title="Delete Student" onclick="Students.deleteStudent(${s.id}, '${s.rollNumber}')">
              <i class="bi bi-trash"></i>
            </button>
          ` : ''}
        </td>
      </tr>
    `).join('');
  },

  openAddModal() {
    document.getElementById('studentModalTitle').textContent = 'Add New Student';
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    this.studentModal.show();
  },

  async openEditModal(id) {
    try {
      const student = await API.get(`/students/${id}`);
      document.getElementById('studentModalTitle').textContent = 'Update Student Details';
      document.getElementById('studentId').value = student.id;
      document.getElementById('studentRoll').value = student.rollNumber;
      document.getElementById('studentName').value = student.fullName;
      document.getElementById('studentEmail').value = student.email;
      document.getElementById('studentPhone').value = student.phoneNumber;
      document.getElementById('studentDept').value = student.department;
      document.getElementById('studentYear').value = student.academicYear;
      document.getElementById('studentSemester').value = student.semester;
      document.getElementById('studentDob').value = student.dateOfBirth;

      this.studentModal.show();
    } catch (err) {
      App.showToast('Failed to fetch student details: ' + err.message, 'danger');
    }
  },

  async handleSave(e) {
    e.preventDefault();
    const id = document.getElementById('studentId').value;

    const payload = {
      rollNumber: document.getElementById('studentRoll').value.trim(),
      fullName: document.getElementById('studentName').value.trim(),
      email: document.getElementById('studentEmail').value.trim(),
      phoneNumber: document.getElementById('studentPhone').value.trim(),
      department: document.getElementById('studentDept').value.trim(),
      academicYear: document.getElementById('studentYear').value.trim(),
      semester: document.getElementById('studentSemester').value.trim(),
      dateOfBirth: document.getElementById('studentDob').value
    };

    try {
      if (id) {
        await API.put(`/students/${id}`, payload);
        App.showToast('Student updated successfully!', 'success');
      } else {
        await API.post('/students', payload);
        App.showToast('Student added successfully!', 'success');
      }

      this.studentModal.hide();
      await this.loadStudents();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast(err.message, 'danger');
    }
  },

  async deleteStudent(id, rollNumber) {
    if (!confirm(`Are you sure you want to delete student ${rollNumber}? All associated marks will also be deleted.`)) {
      return;
    }

    try {
      await API.delete(`/students/${id}`);
      App.showToast(`Student ${rollNumber} deleted successfully.`, 'info');
      await this.loadStudents();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast('Failed to delete student: ' + err.message, 'danger');
    }
  },

  async viewProfile(id) {
    try {
      const student = await API.get(`/students/${id}`);

      document.getElementById('profileRoll').textContent = student.rollNumber;
      document.getElementById('profileName').textContent = student.fullName;
      document.getElementById('profileEmail').textContent = student.email;
      document.getElementById('profilePhone').textContent = student.phoneNumber;
      document.getElementById('profileDept').textContent = student.department;
      document.getElementById('profileYear').textContent = student.academicYear;
      document.getElementById('profileSemester').textContent = student.semester;
      document.getElementById('profileDob').textContent = student.dateOfBirth;

      const viewResultBtn = document.getElementById('profileViewResultBtn');
      if (viewResultBtn) {
        viewResultBtn.onclick = () => {
          this.profileModal.hide();
          App.switchView('results');
          Results.searchResultByRoll(student.rollNumber);
        };
      }

      this.profileModal.show();
    } catch (err) {
      App.showToast('Failed to load profile: ' + err.message, 'danger');
    }
  }
};
