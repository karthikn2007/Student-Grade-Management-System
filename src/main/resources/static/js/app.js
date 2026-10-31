/**
 * Main Application Coordinator - Student Grade Management System
 */
const App = {
  currentView: 'dashboard',

  init() {
    this.setupNavigation();
    this.setupSidebarToggle();

    // Initialize sub-modules
    Auth.init();
    Dashboard.init();
    Students.init();
    Teachers.init();
    Subjects.init();
    Examinations.init();
    Marks.init();
    Results.init();
  },

  setupNavigation() {
    document.querySelectorAll('.sidebar-nav .nav-link[data-view]').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const viewName = link.getAttribute('data-view');
        this.switchView(viewName);
      });
    });
  },

  setupSidebarToggle() {
    const toggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');

    if (toggleBtn && sidebar) {
      toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('show');
      });

      // Close sidebar when clicking outside on mobile
      document.addEventListener('click', (e) => {
        if (window.innerWidth <= 992 &&
            !sidebar.contains(e.target) &&
            !toggleBtn.contains(e.target) &&
            sidebar.classList.contains('show')) {
          sidebar.classList.remove('show');
        }
      });
    }
  },

  switchView(viewName) {
    if (!viewName) return;
    this.currentView = viewName;

    // Update active navigation item
    document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
      if (link.getAttribute('data-view') === viewName) {
        link.classList.add('active');
      } else {
        link.classList.remove('active');
      }
    });

    // Update view containers
    document.querySelectorAll('.view-section').forEach(sec => {
      sec.classList.add('d-none');
    });

    const targetSection = document.getElementById(`view-${viewName}`);
    if (targetSection) {
      targetSection.classList.remove('d-none');
    }

    // Update page title & breadcrumbs
    const titleMap = {
      'dashboard': { title: 'Academic Performance Dashboard', sub: 'Institutional metrics, grade distribution, and pass/fail analytics' },
      'students': { title: 'Student Management', sub: 'Manage student records, enrollment, and profile information' },
      'teachers': { title: 'Faculty Management', sub: 'Manage professors, departments, and academic staff' },
      'subjects': { title: 'Subject & Course Management', sub: 'Course catalog, maximum credits, and passing requirements' },
      'examinations': { title: 'Examination Sessions', sub: 'Manage academic exam schedules, midterm, and final assessments' },
      'marks': { title: 'Marks & Grade Evaluation', sub: 'Enter student marks with real-time grade & pass/fail computation' },
      'results': { title: 'Academic Results & Mark Sheet', sub: 'Search transcript by student roll number and print official mark sheet' }
    };

    const info = titleMap[viewName] || { title: 'Student Grade Management System', sub: '' };
    const pageTitleEl = document.getElementById('topbarPageTitle');
    const pageSubEl = document.getElementById('topbarPageSubtitle');
    if (pageTitleEl) pageTitleEl.textContent = info.title;
    if (pageSubEl) pageSubEl.textContent = info.sub;

    // Load data for the active view
    this.loadActiveView();
  },

  loadActiveView() {
    switch (this.currentView) {
      case 'dashboard':
        Examinations.loadExaminations();
        Dashboard.loadStats();
        break;
      case 'students':
        Students.loadStudents();
        break;
      case 'teachers':
        Teachers.loadTeachers();
        break;
      case 'subjects':
        Subjects.loadSubjects();
        break;
      case 'examinations':
        Examinations.loadExaminations();
        break;
      case 'marks':
        Marks.loadMarksView();
        break;
      case 'results':
        Examinations.loadExaminations();
        break;
    }
  },

  showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toastId = 'toast-' + Date.now();
    const iconMap = {
      'success': 'bi-check-circle-fill text-success',
      'danger': 'bi-exclamation-triangle-fill text-danger',
      'warning': 'bi-exclamation-circle-fill text-warning',
      'info': 'bi-info-circle-fill text-primary'
    };

    const icon = iconMap[type] || iconMap.info;

    const toastHtml = `
      <div id="${toastId}" class="toast align-items-center border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex bg-white rounded">
          <div class="toast-body d-flex align-items-center gap-2">
            <i class="bi ${icon} fs-5"></i>
            <span class="text-dark fw-medium">${message}</span>
          </div>
          <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      </div>
    `;

    container.insertAdjacentHTML('beforeend', toastHtml);
    const toastEl = document.getElementById(toastId);
    const bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
    bsToast.show();

    toastEl.addEventListener('hidden.bs.toast', () => {
      toastEl.remove();
    });
  }
};

// Start application when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  App.init();
});
