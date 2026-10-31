/**
 * Result Management & Printable Mark Sheet Module
 */
const Results = {
  currentResult: null,

  init() {
    const searchForm = document.getElementById('resultSearchForm');
    if (searchForm) {
      searchForm.addEventListener('submit', (e) => this.handleSearch(e));
    }

    const printBtn = document.getElementById('printMarkSheetBtn');
    if (printBtn) {
      printBtn.addEventListener('click', () => window.print());
    }

    // Quick roll buttons
    document.querySelectorAll('.quick-roll-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const roll = e.target.getAttribute('data-roll');
        if (roll) {
          document.getElementById('resultRollInput').value = roll;
          this.searchResultByRoll(roll);
        }
      });
    });
  },

  async handleSearch(e) {
    if (e) e.preventDefault();
    const roll = document.getElementById('resultRollInput')?.value.trim();
    if (!roll) {
      App.showToast('Please enter a student roll number.', 'warning');
      return;
    }
    await this.searchResultByRoll(roll);
  },

  async searchResultByRoll(rollNumber) {
    const examSelect = document.getElementById('resultsExamSelect');
    const examId = examSelect ? examSelect.value : '';
    const input = document.getElementById('resultRollInput');
    if (input) input.value = rollNumber;

    const endpoint = examId
      ? `/results/student/${encodeURIComponent(rollNumber)}?examId=${examId}`
      : `/results/student/${encodeURIComponent(rollNumber)}`;

    try {
      const result = await API.get(endpoint);
      this.currentResult = result;
      this.renderMarkSheet(result);
      document.getElementById('markSheetContainer').classList.remove('d-none');
      document.getElementById('resultNotFoundContainer').classList.add('d-none');
    } catch (err) {
      this.currentResult = null;
      document.getElementById('markSheetContainer').classList.add('d-none');
      document.getElementById('resultNotFoundContainer').classList.remove('d-none');
      document.getElementById('resultNotFoundMessage').textContent = err.message || 'No result found for the specified roll number and examination.';
    }
  },

  renderMarkSheet(res) {
    // Student Details
    document.getElementById('msCollegeName').textContent = 'METROPOLITAN INSTITUTE OF TECHNOLOGY & SCIENCE';
    document.getElementById('msStudentName').textContent = res.fullName;
    document.getElementById('msRollNumber').textContent = res.rollNumber;
    document.getElementById('msDepartment').textContent = res.department;
    document.getElementById('msAcademicYear').textContent = res.academicYear;
    document.getElementById('msSemester').textContent = res.semester;
    document.getElementById('msDob').textContent = res.dateOfBirth;

    // Examination Details
    document.getElementById('msExamName').textContent = res.examName;
    document.getElementById('msExamCode').textContent = res.examCode;
    document.getElementById('msIssueDate').textContent = new Date().toLocaleDateString('en-US', {
      year: 'numeric', month: 'long', day: 'numeric'
    });

    // Subject Breakdown
    const tbody = document.getElementById('msSubjectsTableBody');
    if (tbody) {
      if (!res.subjectMarks || res.subjectMarks.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center py-3 text-muted">No marks recorded for this examination.</td></tr>`;
      } else {
        tbody.innerHTML = res.subjectMarks.map((s, idx) => `
          <tr>
            <td>${idx + 1}</td>
            <td class="fw-bold">${s.subjectCode}</td>
            <td class="text-start">${s.subjectName}</td>
            <td>${s.maxMarks}</td>
            <td>${s.passingMarks}</td>
            <td class="fw-bold fs-6">${s.marksObtained}</td>
            <td>
              <span class="badge badge-grade badge-grade-${s.grade.toLowerCase().replace('+', 'plus')}">
                ${s.grade}
              </span>
            </td>
            <td>
              <span class="badge badge-status ${s.status === 'PASS' ? 'badge-pass' : 'badge-fail'}">
                ${s.status}
              </span>
            </td>
          </tr>
        `).join('');
      }
    }

    // Summary Box
    document.getElementById('msTotalMarks').textContent = `${res.totalMarksObtained} / ${res.totalMaxMarks}`;
    document.getElementById('msPercentage').textContent = `${res.percentage}%`;

    const gradeEl = document.getElementById('msOverallGrade');
    gradeEl.textContent = res.overallGrade;
    gradeEl.className = `badge badge-grade badge-grade-${(res.overallGrade || 'F').toLowerCase().replace('+', 'plus')} fs-6 px-3 py-1`;

    const statusEl = document.getElementById('msOverallStatus');
    statusEl.textContent = res.overallStatus;
    statusEl.className = `badge badge-status ${res.overallStatus === 'PASS' ? 'badge-pass' : 'badge-fail'} fs-6 px-3 py-1`;

    // Watermark
    const watermarkEl = document.getElementById('msWatermark');
    if (watermarkEl) {
      watermarkEl.textContent = res.overallStatus === 'PASS' ? 'PASSED' : 'FAILED';
    }
  }
};
