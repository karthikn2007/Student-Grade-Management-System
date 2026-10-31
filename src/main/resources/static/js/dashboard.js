/**
 * Dashboard Analytics & Visualizations Module
 */
const Dashboard = {
  gradeChartInstance: null,
  passFailChartInstance: null,
  deptChartInstance: null,

  init() {
    const examSelect = document.getElementById('dashboardExamFilter');
    if (examSelect) {
      examSelect.addEventListener('change', () => this.loadStats());
    }
  },

  async loadStats() {
    try {
      const examSelect = document.getElementById('dashboardExamFilter');
      const examId = examSelect ? examSelect.value : '';
      const endpoint = examId ? `/dashboard/stats?examId=${examId}` : '/dashboard/stats';

      const stats = await API.get(endpoint);
      this.renderStatCards(stats);
      this.renderCharts(stats);
    } catch (err) {
      console.error('Error loading dashboard stats:', err);
    }
  },

  renderStatCards(stats) {
    const setVal = (id, val) => {
      const el = document.getElementById(id);
      if (el) el.textContent = val;
    };

    setVal('statTotalStudents', stats.totalStudents || 0);
    setVal('statTotalTeachers', stats.totalTeachers || 0);
    setVal('statTotalSubjects', stats.totalSubjects || 0);
    setVal('statTotalExams', stats.totalExaminations || 0);
    setVal('statClassAverage', (stats.averageClassPercentage || 0).toFixed(1) + '%');
    setVal('statPassedStudents', stats.passedStudentsCount || 0);
    setVal('statFailedStudents', stats.failedStudentsCount || 0);
  },

  renderCharts(stats) {
    this.renderGradeDistributionChart(stats.gradeDistribution || {});
    this.renderPassFailChart(stats.passedStudentsCount || 0, stats.failedStudentsCount || 0);
    this.renderDepartmentChart(stats.departmentPerformance || {});
  },

  renderGradeDistributionChart(dist) {
    const ctx = document.getElementById('gradeDistChart');
    if (!ctx) return;

    const labels = ['A+', 'A', 'B', 'C', 'D', 'E', 'F'];
    const dataValues = labels.map(g => dist[g] || 0);

    const colors = [
      '#1e40af', // A+ deep blue
      '#0284c7', // A sky blue
      '#10b981', // B green
      '#eab308', // C yellow
      '#f97316', // D orange
      '#a855f7', // E purple
      '#ef4444'  // F red
    ];

    if (this.gradeChartInstance) {
      this.gradeChartInstance.destroy();
    }

    this.gradeChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Number of Students',
          data: dataValues,
          backgroundColor: colors,
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => ` ${context.raw} student(s)`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 }
          }
        }
      }
    });
  },

  renderPassFailChart(passed, failed) {
    const ctx = document.getElementById('passFailChart');
    if (!ctx) return;

    if (this.passFailChartInstance) {
      this.passFailChartInstance.destroy();
    }

    const total = passed + failed;
    const hasData = total > 0;

    this.passFailChartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Passed', 'Failed'],
        datasets: [{
          data: hasData ? [passed, failed] : [1, 0],
          backgroundColor: hasData ? ['#10b981', '#ef4444'] : ['#e2e8f0', '#cbd5e1'],
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: { boxWidth: 14, font: { size: 12 } }
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                if (!hasData) return ' No evaluation data';
                const count = context.raw;
                const pct = ((count / total) * 100).toFixed(1);
                return ` ${context.label}: ${count} (${pct}%)`;
              }
            }
          }
        },
        cutout: '68%'
      }
    });
  },

  renderDepartmentChart(deptPerf) {
    const ctx = document.getElementById('deptPerfChart');
    if (!ctx) return;

    const labels = Object.keys(deptPerf);
    const dataValues = Object.values(deptPerf);

    if (this.deptChartInstance) {
      this.deptChartInstance.destroy();
    }

    this.deptChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels.length ? labels : ['No Data'],
        datasets: [{
          label: 'Average Score (%)',
          data: dataValues.length ? dataValues : [0],
          backgroundColor: '#3b82f6',
          borderRadius: 6
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => ` Average: ${context.raw}%`
            }
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            max: 100,
            ticks: {
              callback: (val) => `${val}%`
            }
          }
        }
      }
    });
  }
};
