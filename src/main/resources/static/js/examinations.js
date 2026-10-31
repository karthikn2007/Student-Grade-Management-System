/**
 * Examination Management Module
 */
const Examinations = {
  examsList: [],
  examModal: null,

  init() {
    const modalEl = document.getElementById('examModal');
    if (modalEl) this.examModal = new bootstrap.Modal(modalEl);

    const form = document.getElementById('examForm');
    if (form) {
      form.addEventListener('submit', (e) => this.handleSave(e));
    }

    const addBtn = document.getElementById('addExamBtn');
    if (addBtn) {
      addBtn.addEventListener('click', () => this.openAddModal());
    }

    const searchInput = document.getElementById('examSearchInput');
    if (searchInput) {
      searchInput.addEventListener('input', () => this.filterExams());
    }
  },

  async loadExaminations() {
    try {
      const data = await API.get('/examinations');
      this.examsList = data || [];
      this.renderTable(this.examsList);
      this.populateExamSelectors();
    } catch (err) {
      App.showToast('Failed to load examinations: ' + err.message, 'danger');
    }
  },

  populateExamSelectors() {
    const selectors = ['dashboardExamFilter', 'marksExamSelect', 'resultsExamSelect'];
    selectors.forEach(selectId => {
      const el = document.getElementById(selectId);
      if (!el) return;

      const currentVal = el.value;
      let optionsHtml = '';

      if (selectId === 'dashboardExamFilter' || selectId === 'resultsExamSelect') {
        optionsHtml += '<option value="">Latest / All Examinations</option>';
      } else {
        optionsHtml += '<option value="">Select Examination</option>';
      }

      optionsHtml += this.examsList.map(e =>
        `<option value="${e.id}">${e.examName} (${e.academicYear})</option>`
      ).join('');

      el.innerHTML = optionsHtml;
      if (currentVal) el.value = currentVal;
    });
  },

  filterExams() {
    const searchVal = (document.getElementById('examSearchInput')?.value || '').toLowerCase().trim();
    const filtered = this.examsList.filter(e =>
      !searchVal ||
      e.examCode.toLowerCase().includes(searchVal) ||
      e.examName.toLowerCase().includes(searchVal) ||
      e.academicYear.toLowerCase().includes(searchVal)
    );
    this.renderTable(filtered);
  },

  renderTable(exams) {
    const tbody = document.getElementById('examsTableBody');
    if (!tbody) return;

    if (!exams || exams.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="text-center py-4 text-muted">
            <i class="bi bi-calendar-event fs-2 d-block mb-2 text-secondary"></i>
            No examinations found.
          </td>
        </tr>
      `;
      return;
    }

    const isAdmin = Auth.isAdmin();

    tbody.innerHTML = exams.map((e, idx) => `
      <tr>
        <td class="fw-semibold text-muted">${idx + 1}</td>
        <td>
          <span class="badge bg-light text-primary border fw-bold">${e.examCode}</span>
        </td>
        <td><div class="fw-bold text-dark">${e.examName}</div></td>
        <td>${e.academicYear}</td>
        <td>${e.semester}</td>
        <td>
          <small class="text-muted">
            ${e.startDate ? e.startDate : 'TBD'} &mdash; ${e.endDate ? e.endDate : 'TBD'}
          </small>
        </td>
        <td class="text-end">
          ${isAdmin ? `
            <button class="btn-action admin-only" title="Edit Exam" onclick="Examinations.openEditModal(${e.id})">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn-action btn-action-delete admin-only" title="Delete Exam" onclick="Examinations.deleteExam(${e.id}, '${e.examCode}')">
              <i class="bi bi-trash"></i>
            </button>
          ` : '<span class="text-muted small">View Only</span>'}
        </td>
      </tr>
    `).join('');
  },

  openAddModal() {
    document.getElementById('examModalTitle').textContent = 'Add Examination Session';
    document.getElementById('examForm').reset();
    document.getElementById('examId').value = '';
    this.examModal.show();
  },

  async openEditModal(id) {
    try {
      const exam = await API.get(`/examinations/${id}`);
      document.getElementById('examModalTitle').textContent = 'Update Examination';
      document.getElementById('examId').value = exam.id;
      document.getElementById('examCode').value = exam.examCode;
      document.getElementById('examName').value = exam.examName;
      document.getElementById('examAcademicYear').value = exam.academicYear;
      document.getElementById('examSemester').value = exam.semester;
      document.getElementById('examStartDate').value = exam.startDate || '';
      document.getElementById('examEndDate').value = exam.endDate || '';

      this.examModal.show();
    } catch (err) {
      App.showToast('Failed to fetch examination: ' + err.message, 'danger');
    }
  },

  async handleSave(e) {
    e.preventDefault();
    const id = document.getElementById('examId').value;

    const payload = {
      examCode: document.getElementById('examCode').value.trim(),
      examName: document.getElementById('examName').value.trim(),
      academicYear: document.getElementById('examAcademicYear').value.trim(),
      semester: document.getElementById('examSemester').value.trim(),
      startDate: document.getElementById('examStartDate').value || null,
      endDate: document.getElementById('examEndDate').value || null
    };

    try {
      if (id) {
        await API.put(`/examinations/${id}`, payload);
        App.showToast('Examination updated successfully!', 'success');
      } else {
        await API.post('/examinations', payload);
        App.showToast('Examination added successfully!', 'success');
      }

      this.examModal.hide();
      await this.loadExaminations();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast(err.message, 'danger');
    }
  },

  async deleteExam(id, code) {
    if (!confirm(`Are you sure you want to delete examination "${code}"? All marks recorded under this exam will be deleted.`)) {
      return;
    }

    try {
      await API.delete(`/examinations/${id}`);
      App.showToast(`Examination "${code}" deleted successfully.`, 'info');
      await this.loadExaminations();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast('Failed to delete examination: ' + err.message, 'danger');
    }
  }
};
