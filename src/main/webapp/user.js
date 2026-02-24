// user.js — logic for user.jsp (post jobs, manage own jobs)

document.addEventListener('DOMContentLoaded', loadMyJobs);

// ── SUBMIT NEW JOB ────────────────────────────────────────────
async function submitJob(e) {
    e.preventDefault();
    const title = document.getElementById('f-title').value.trim();
    const desc = document.getElementById('f-desc').value.trim();
    if (!title || !desc) { toast('Title and Description are required'); return; }

    const body = {
        title,
        description: desc,
        category: document.getElementById('f-category').value,
        location: document.getElementById('f-location').value.trim(),
        payRate: document.getElementById('f-pay').value.trim(),
        postedBy: USERNAME
    };

    try {
        await api('/jobs', 'POST', body);
        toast('Job posted successfully!');
        document.getElementById('job-form').reset();
        loadMyJobs();
    } catch (err) {
        toast(err.message || 'Failed to post job');
    }
}

// ── LOAD MY POSTED JOBS ───────────────────────────────────────
async function loadMyJobs() {
    const tbody = document.getElementById('my-jobs-tbody');
    tbody.innerHTML = `<tr><td colspan="4" class="empty">Loading…</td></tr>`;
    try {
        const jobs = await api(`/jobs/posted/${encodeURIComponent(USERNAME)}`);
        if (!jobs || jobs.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="empty">No jobs posted yet</td></tr>`;
            return;
        }
        tbody.innerHTML = jobs.map(j => `
      <tr>
        <td>${x(j.title)}</td>
        <td><span class="badge badge-${j.status}">${j.status}</span></td>
        <td>${j.acceptedByUsername ? x(j.acceptedByUsername) : '—'}</td>
        <td>
          <div class="act-group">
            ${j.status === 'AVAILABLE' ? `<button class="sm" onclick="openEditModal(${j.id})">Edit</button>` : ''}
            <button class="sm danger" onclick="deleteJob(${j.id})">Delete</button>
          </div>
        </td>
      </tr>`).join('');
    } catch {
        tbody.innerHTML = `<tr><td colspan="4" class="empty">Failed to load jobs</td></tr>`;
    }
}

// ── EDIT MODAL ────────────────────────────────────────────────
async function openEditModal(id) {
    try {
        const j = await api(`/jobs/${id}`);
        document.getElementById('edit-id').value = j.id;
        document.getElementById('edit-title').value = j.title;
        document.getElementById('edit-category').value = j.category || '';
        document.getElementById('edit-location').value = j.location || '';
        document.getElementById('edit-pay').value = j.payRate || '';
        document.getElementById('edit-desc').value = j.description;
        document.getElementById('edit-modal').classList.add('open');
    } catch {
        toast('Could not load job details');
    }
}

function closeEditModal() {
    document.getElementById('edit-modal').classList.remove('open');
}

async function saveEdit() {
    const id = document.getElementById('edit-id').value;
    const body = {
        title: document.getElementById('edit-title').value.trim(),
        description: document.getElementById('edit-desc').value.trim(),
        category: document.getElementById('edit-category').value,
        location: document.getElementById('edit-location').value.trim(),
        payRate: document.getElementById('edit-pay').value.trim(),
        postedBy: USERNAME
    };
    if (!body.title || !body.description) { toast('Title and Description required'); return; }
    try {
        await api(`/jobs/${id}`, 'PUT', body);
        toast('Job updated');
        closeEditModal();
        loadMyJobs();
    } catch (err) {
        toast(err.message || 'Update failed');
    }
}

// ── DELETE JOB ────────────────────────────────────────────────
async function deleteJob(id) {
    if (!confirm('Delete this job?')) return;
    try {
        await api(`/jobs/${id}`, 'DELETE');
        toast('Job deleted');
        loadMyJobs();
    } catch {
        toast('Failed to delete job');
    }
}

// ── SHARED UTILS ─────────────────────────────────────────────
async function api(path, method = 'GET', body = null) {
    const token = sessionStorage.getItem('jwt');
    const headers = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
    if (token) headers['Authorization'] = 'Bearer ' + token;
    const res = await fetch(API + path, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null
    });
    const text = await res.text();
    let data; try { data = JSON.parse(text); } catch { data = { message: text }; }
    if (!res.ok) throw new Error(data?.message || `HTTP ${res.status}`);
    return data;
}

let toastTimer;
function toast(msg) {
    const el = document.getElementById('toast');
    el.textContent = msg;
    el.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => el.classList.remove('show'), 3000);
}

function x(s) {
    return String(s ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
