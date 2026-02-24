const API = '/project-1.0-SNAPSHOT/api';

let allJobs = [], allUsers = [];

// ── TABS ─────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.tab-btn').forEach(btn =>
    btn.addEventListener('click', () => {
      document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
      btn.classList.add('active');
      document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
      if (btn.dataset.tab === 'jobs') loadJobs();
      if (btn.dataset.tab === 'users') loadUsers();
    })
  );
  loadJobs();
});

function me() { return document.getElementById('session-username').value.trim() || 'guest'; }

// ── JOBS ─────────────────────────────────────────────────────
async function loadJobs() {
  const status = document.getElementById('job-status-filter').value;
  const url = status ? `/jobs/status/${status}` : '/jobs';
  try {
    allJobs = await api(url) || [];
    renderJobs(allJobs);
  } catch { toast('Failed to load jobs'); }
}

function renderJobs(list) {
  const tb = document.getElementById('jobs-tbody');
  if (!list.length) {
    tb.innerHTML = `<tr><td colspan="9" class="empty">No jobs found</td></tr>`;
    return;
  }
  tb.innerHTML = list.map(j => `
    <tr>
      <td>${j.id}</td>
      <td>${x(j.title)}</td>
      <td>${x(j.category || '—')}</td>
      <td>${x(j.location || '—')}</td>
      <td>${x(j.payRate || '—')}</td>
      <td>${x(j.postedBy)}</td>
      <td><span class="badge badge-${j.status}">${j.status}</span></td>
      <td>${j.acceptedByUsername ? x(j.acceptedByUsername) : '—'}</td>
      <td>
        <div class="act-group">
          <button class="sm" onclick="editJob(${j.id})">Edit</button>
          ${j.status === 'AVAILABLE' ? `<button class="sm accept" onclick="acceptJob(${j.id})">Accept</button>` : ''}
          <button class="sm danger" onclick="deleteJob(${j.id})">Delete</button>
        </div>
      </td>
    </tr>`).join('');
}

function openJobModal(job = null) {
  document.getElementById('job-modal-title').textContent = job ? 'Edit Job' : 'New Job';
  document.getElementById('job-id').value = job?.id || '';
  document.getElementById('job-title').value = job?.title || '';
  document.getElementById('job-description').value = job?.description || '';
  document.getElementById('job-category').value = job?.category || '';
  document.getElementById('job-location').value = job?.location || '';
  document.getElementById('job-payrate').value = job?.payRate || '';
  document.getElementById('job-postedby').value = job?.postedBy || me();
  document.getElementById('posted-by-label').style.display = job ? 'none' : '';
  document.getElementById('job-modal').classList.add('open');
}
function closeJobModal() { document.getElementById('job-modal').classList.remove('open'); }

async function saveJob() {
  const id = document.getElementById('job-id').value;
  const title = document.getElementById('job-title').value.trim();
  const desc = document.getElementById('job-description').value.trim();
  if (!title || !desc) { toast('Title and Description are required'); return; }

  const body = {
    title, description: desc,
    category: document.getElementById('job-category').value,
    location: document.getElementById('job-location').value.trim(),
    payRate: document.getElementById('job-payrate').value.trim(),
    postedBy: document.getElementById('job-postedby').value.trim() || me()
  };
  try {
    id ? await api(`/jobs/${id}`, 'PUT', body) : await api('/jobs', 'POST', body);
    toast(id ? 'Job updated' : 'Job created');
    closeJobModal();
    loadJobs();
  } catch (e) { toast(e.message || 'Error saving job'); }
}

async function editJob(id) {
  try { openJobModal(await api(`/jobs/${id}`)); }
  catch { toast('Could not load job'); }
}

async function acceptJob(id) {
  const worker = me();
  if (worker === 'guest') { toast('Set your username first'); return; }
  try {
    const r = await api(`/jobs/${id}/accept?worker=${encodeURIComponent(worker)}`, 'PUT');
    toast(r.message || 'Accepted');
    loadJobs();
  } catch (e) { toast(e.message || 'Could not accept'); }
}

async function deleteJob(id) {
  if (!confirm('Delete this job?')) return;
  try { await api(`/jobs/${id}`, 'DELETE'); toast('Deleted'); loadJobs(); }
  catch { toast('Error deleting job'); }
}

// ── USERS ────────────────────────────────────────────────────
async function loadUsers() {
  const role = document.getElementById('user-role-filter').value;
  const url = role ? `/users/role/${role}` : '/users';
  try {
    allUsers = await api(url) || [];
    renderUsers(allUsers);
  } catch { toast('Failed to load users'); }
}

function renderUsers(list) {
  const tb = document.getElementById('users-tbody');
  if (!list.length) {
    tb.innerHTML = `<tr><td colspan="6" class="empty">No users found</td></tr>`;
    return;
  }
  tb.innerHTML = list.map(u => `
    <tr>
      <td>${u.id}</td>
      <td>${x(u.username)}</td>
      <td>${x(u.fullName)}</td>
      <td>${x(u.email)}</td>
      <td><span class="badge badge-${u.role}">${u.role}</span></td>
      <td>
        <div class="act-group">
          <button class="sm" onclick="editUser(${u.id})">Edit</button>
          <button class="sm danger" onclick="deleteUser(${u.id})">Delete</button>
        </div>
      </td>
    </tr>`).join('');
}

function openUserModal(user = null) {
  document.getElementById('user-modal-title').textContent = user ? 'Edit User' : 'New User';
  document.getElementById('user-id').value = user?.id || '';
  document.getElementById('user-username').value = user?.username || '';
  document.getElementById('user-fullname').value = user?.fullName || '';
  document.getElementById('user-email').value = user?.email || '';
  document.getElementById('user-role').value = user?.role || 'USER';
  document.getElementById('user-username').disabled = !!user;
  document.getElementById('user-modal').classList.add('open');
}
function closeUserModal() {
  document.getElementById('user-username').disabled = false;
  document.getElementById('user-modal').classList.remove('open');
}

async function saveUser() {
  const id = document.getElementById('user-id').value;
  const username = document.getElementById('user-username').value.trim();
  const fullName = document.getElementById('user-fullname').value.trim();
  const email = document.getElementById('user-email').value.trim();
  if (!username || !fullName || !email) { toast('All fields are required'); return; }

  const body = { username, fullName, email, role: document.getElementById('user-role').value };
  try {
    id ? await api(`/users/${id}`, 'PUT', body) : await api('/users', 'POST', body);
    toast(id ? 'User updated' : 'User created');
    closeUserModal();
    loadUsers();
  } catch (e) { toast(e.message || 'Error saving user'); }
}

async function editUser(id) {
  try { openUserModal(await api(`/users/${id}`)); }
  catch { toast('Could not load user'); }
}

async function deleteUser(id) {
  if (!confirm('Delete this user?')) return;
  try { await api(`/users/${id}`, 'DELETE'); toast('Deleted'); loadUsers(); }
  catch { toast('Error deleting user'); }
}

// ── HTTP ─────────────────────────────────────────────────────
async function api(path, method = 'GET', body = null) {
  const res = await fetch(API + path, {
    method,
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    body: body ? JSON.stringify(body) : null
  });
  const text = await res.text();
  let data; try { data = JSON.parse(text); } catch { data = { message: text }; }
  if (!res.ok) { const e = new Error(data?.message || `HTTP ${res.status}`); throw e; }
  return data;
}

// ── TOAST ─────────────────────────────────────────────────────
let toastTimer;
function toast(msg) {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.classList.add('show');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.remove('show'), 3000);
}

// ── UTIL ──────────────────────────────────────────────────────
function x(s) {
  return String(s ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
