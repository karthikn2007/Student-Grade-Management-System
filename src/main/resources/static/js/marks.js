/**
 * Marks & Grade Management Module
 */
const Marks = {
  marksList: [],
  selectedSubject: null,

  init() {
    const form = document.getElementById('marksEntryForm');
    if (form) {
      form.addEventListener('submit', (e) => this.handleSubmit(e));
    }

    const marksInput = document.getElementById('marksObtainedInput');
    if (marksInput) {
      marksInput.addEventListener('input', () => this.handleLiveCalculation());
    }

    const subjectSelect = document.getElementById('marksSubjectSelect');
    if (subjectSelect) {
      subjectSelect.addEventListener('change', () => this.handleSubjectChange());
    }

    const filterExam = document.getElementById('filterMarksExam');
    if (filterExam) {
      filterExam.addEventListener('change', () => this.filterMarksTable());
    }

    const filterSubject = document.getElementById('filterMarksSubject');
    if (filterSubject) {
      filterSubject.addEventListener('change', () => this.filterMarksTable());
    }

    const resetBtn = document.getElementById('resetMarksFormBtn');
    if (resetBtn) {
      resetBtn.addEventListener('click', () => this.resetForm());
    }
  },

  async loadMarksView() {
    await Promise.all([
      this.populateDropdowns(),
      this.loadMarksTable()
    ]);
  },

  async populateDropdowns() {
    // 1. Populate Examinations
    const examSelect = document.getElementById('marksExamSelect');
    const filterExam = document.getElementById('filterMarksExam');
    if (examSelect && Examinations.examsList) {
      const opts = Examinations.examsList.map(e =>
        `<option value="${e.id}">${e.examName} (${e.academicYear})</option>`
      ).join('');
      examSelect.innerHTML = '<option value="">-- Choose Examination --</option>' + opts;
      if (filterExam) {
        filterExam.innerHTML = '<option value="">All Examinations</option>' + opts;
      }
    }

    // 2. Populate Subjects
    const subjectSelect = document.getElementById('marksSubjectSelect');
    const filterSubject = document.getElementById('filterMarksSubject');
    if (subjectSelect && Subjects.subjectsList) {
      const opts = Subjects.subjectsList.map(s =>
        `<option value="${s.id}" data-max="${s.maxMarks}" data-pass="${s.passingMarks}">
          ${s.subjectCode} - ${s.subjectName} (Max: ${s.maxMarks}, Pass: ${s.passingMarks})
        </option>`
      ).join('');
      subjectSelect.innerHTML = '<option value="">-- Choose Subject --</option>' + opts;
      if (filterSubject) {
        filterSubject.innerHTML = '<option value="">All Subjects</option>' +
          Subjects.subjectsList.map(s => `<option value="${s.id}">${s.subjectCode} - ${s.subjectName}</option>`).join('');
      }
    }

    // 3. Populate Students
    const studentSelect = document.getElementById('marksStudentSelect');
    if (studentSelect && Students.studentsList) {
      const opts = Students.studentsList.map(s =>
        `<option value="${s.id}">
          [${s.rollNumber}] ${s.fullName} (${s.department} - ${s.semester})
        </option>`
      ).join('');
      studentSelect.innerHTML = '<option value="">-- Choose Student --</option>' + opts;
    }
  },

  handleSubjectChange() {
    const subjectSelect = document.getElementById('marksSubjectSelect');
    const selectedOption = subjectSelect.options[subjectSelect.selectedIndex];

    if (selectedOption && selectedOption.value) {
      this.selectedSubject = {
        id: selectedOption.value,
        maxMarks: parseFloat(selectedOption.getAttribute('data-max') || '100'),
        passingMarks: parseFloat(selectedOption.getAttribute('data-pass') || '40')
      };
      document.getElementById('marksMaxDisplay').textContent = this.selectedSubject.maxMarks;
      document.getElementById('marksPassDisplay').textContent = this.selectedSubject.passingMarks;
      document.getElementById('marksObtainedInput').max = this.selectedSubject.maxMarks;
    } else {
      this.selectedSubject = null;
      document.getElementById('marksMaxDisplay').textContent = '--';
      document.getElementById('marksPassDisplay').textContent = '--';
    }

    this.handleLiveCalculation();
  },

  handleLiveCalculation() {
    const marksInput = document.getElementById('marksObtainedInput');
    const marksVal = parseFloat(marksInput.value);

    const pctEl = document.getElementById('calcPercentageDisplay');
    const gradeEl = document.getElementById('calcGradeDisplay');
    const statusEl = document.getElementById('calcStatusDisplay');
    const errorEl = document.getElementById('marksCalcError');

    errorEl.classList.add('d-none');
    errorEl.textContent = '';

    if (isNaN(marksVal)) {
      pctEl.textContent = '--';
      gradeEl.textContent = '--';
      statusEl.textContent = '--';
      statusEl.className = 'badge bg-light text-secondary border';
      return;
    }

    const max = this.selectedSubject ? this.selectedSubject.maxMarks : 100.0;
    const pass = this.selectedSubject ? this.selectedSubject.passingMarks : 40.0;

    // Boundary checks
    if (marksVal < 0) {
      errorEl.textContent = 'Marks cannot be negative!';
      errorEl.classList.remove('d-none');
      return;
    }

    if (marksVal > max) {
      errorEl.textContent = `Marks cannot exceed maximum marks (${max})!`;
      errorEl.classList.remove('d-none');
      return;
    }

    const pct = (marksVal / max) * 100.0;
    const grade = this.calculateGrade(pct);
    const passed = marksVal >= pass;

    pctEl.textContent = pct.toFixed(1) + '%';
    gradeEl.textContent = grade;
    gradeEl.className = `badge badge-grade badge-grade-${grade.toLowerCase().replace('+', 'plus')}`;

    statusEl.textContent = passed ? 'PASS' : 'FAIL';
    statusEl.className = `badge badge-status ${passed ? 'badge-pass' : 'badge-fail'}`;
  },

  calculateGrade(percentage) {
    if (percentage >= 90.0) return 'A+';
    if (percentage >= 80.0) return 'A';
    if (percentage >= 70.0) return 'B';
    if (percentage >= 60.0) return 'C';
    if (percentage >= 50.0) return 'D';
    if (percentage >= 40.0) return 'E';
    return 'F';
  },

  async handleSubmit(e) {
    e.preventDefault();

    const markId = document.getElementById('markRecordId').value;
    const studentId = document.getElementById('marksStudentSelect').value;
    const subjectId = document.getElementById('marksSubjectSelect').value;
    const examinationId = document.getElementById('marksExamSelect').value;
    const marksVal = parseFloat(document.getElementById('marksObtainedInput').value);
    const remarks = document.getElementById('marksRemarksInput').value.trim();

    if (!studentId || !subjectId || !examinationId || isNaN(marksVal)) {
      App.showToast('Please fill in all required fields.', 'warning');
      return;
    }

    const max = this.selectedSubject ? this.selectedSubject.maxMarks : 100.0;
    if (marksVal < 0 || marksVal > max) {
      App.showToast(`Marks must be between 0 and ${max}!`, 'danger');
      return;
    }

    const payload = {
      id: markId ? parseInt(markId) : null,
      studentId: parseInt(studentId),
      subjectId: parseInt(subjectId),
      examinationId: parseInt(examinationId),
      marksObtained: marksVal,
      remarks: remarks
    };

    try {
      await API.post('/marks', payload);
      App.showToast('Marks and grade recorded successfully!', 'success');
      this.resetForm();
      await this.loadMarksTable();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast(err.message, 'danger');
    }
  },

  resetForm() {
    document.getElementById('marksEntryForm').reset();
    document.getElementById('markRecordId').value = '';
    document.getElementById('marksEntrySubmitBtn').innerHTML = '<i class="bi bi-check-circle me-1"></i> Save Marks & Grade';
    this.selectedSubject = null;
    document.getElementById('marksMaxDisplay').textContent = '--';
    document.getElementById('marksPassDisplay').textContent = '--';
    document.getElementById('calcPercentageDisplay').textContent = '--';
    document.getElementById('calcGradeDisplay').textContent = '--';
    document.getElementById('calcStatusDisplay').textContent = '--';
    document.getElementById('calcStatusDisplay').className = 'badge bg-light text-secondary border';
    document.getElementById('marksCalcError').classList.add('d-none');
  },

  async loadMarksTable() {
    try {
      const data = await API.get('/marks');
      this.marksList = data || [];
      this.filterMarksTable();
    } catch (err) {
      console.error('Failed to load marks table:', err);
    }
  },

  filterMarksTable() {
    const examFilter = document.getElementById('filterMarksExam')?.value;
    const subjectFilter = document.getElementById('filterMarksSubject')?.value;

    const filtered = this.marksList.filter(m => {
      const matchesExam = !examFilter || String(m.examinationId) === String(examFilter);
      const matchesSubject = !subjectFilter || String(m.subjectId) === String(subjectFilter);
      return matchesExam && matchesSubject;
    });

    this.renderMarksTable(filtered);
  },

  renderMarksTable(marks) {
    const tbody = document.getElementById('marksTableBody');
    if (!tbody) return;

    if (!marks || marks.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="9" class="text-center py-4 text-muted">
            <i class="bi bi-file-earmark-ruled fs-2 d-block mb-2 text-secondary"></i>
            No marks records found for this filter.
          </td>
        </tr>
      `;
      return;
    }

    const isAdmin = Auth.isAdmin();

    tbody.innerHTML = marks.map((m, idx) => `
      <tr>
        <td class="fw-semibold text-muted">${idx + 1}</td>
        <td>
          <span class="badge bg-light text-primary border fw-bold">${m.studentRollNumber}</span>
          <div class="small fw-semibold text-dark">${m.studentName}</div>
        </td>
        <td>
          <div class="fw-semibold">${m.subjectName}</div>
          <small class="text-muted">${m.subjectCode}</small>
        </td>
        <td><small class="text-muted">${m.examName}</small></td>
        <td>
          <span class="fw-bold fs-6">${m.marksObtained}</span>
          <span class="text-muted small">/ ${m.maxMarks}</span>
        </td>
        <td>
          <span class="badge badge-grade badge-grade-${m.grade.toLowerCase().replace('+', 'plus')}">
            ${m.grade}
          </span>
        </td>
        <td>
          <span class="badge badge-status ${m.status === 'PASS' ? 'badge-pass' : 'badge-fail'}">
            ${m.status}
          </span>
        </td>
        <td><small class="text-muted">${m.remarks || '-'}</small></td>
        <td class="text-end">
          <button class="btn-action" title="Edit Mark" onclick="Marks.editMark(${m.id})">
            <i class="bi bi-pencil"></i>
          </button>
          ${isAdmin ? `
            <button class="btn-action btn-action-delete admin-only" title="Delete Mark" onclick="Marks.deleteMark(${m.id})">
              <i class="bi bi-trash"></i>
            </button>
          ` : ''}
        </td>
      </tr>
    `).join('');
  },

  async editMark(id) {
    try {
      const mark = await API.get(`/marks/${id}`);
      document.getElementById('markRecordId').value = mark.id;
      document.getElementById('marksStudentSelect').value = mark.studentId;
      document.getElementById('marksExamSelect').value = mark.examinationId;
      document.getElementById('marksSubjectSelect').value = mark.subjectId;
      this.handleSubjectChange();

      document.getElementById('marksObtainedInput').value = mark.marksObtained;
      document.getElementById('marksRemarksInput').value = mark.remarks || '';
      document.getElementById('marksEntrySubmitBtn').innerHTML = '<i class="bi bi-check-circle me-1"></i> Update Mark Record';

      this.handleLiveCalculation();

      // Scroll smoothly to form
      document.getElementById('marksEntryCard')?.scrollIntoView({ behavior: 'smooth' });
    } catch (err) {
      App.showToast('Failed to fetch mark record: ' + err.message, 'danger');
    }
  },

  async deleteMark(id) {
    if (!confirm('Are you sure you want to delete this mark record?')) return;

    try {
      await API.delete(`/marks/${id}`);
      App.showToast('Mark record deleted successfully.', 'info');
      await this.loadMarksTable();
      Dashboard.loadStats();
    } catch (err) {
      App.showToast('Failed to delete mark record: ' + err.message, 'danger');
    }
  }
};
