// worker.js — logic for worker.jsp (browse + accept jobs)

document.addEventListener('DOMContentLoaded', () => {
    loadAvailable();
    loadAccepted();
});

// ── AVAILABLE JOBS ────────────────────────────────────────────
async function loadAvailable() {
    const tbody = document.getElementById('avail-tbody');
    tbody.innerHTML = `<tr><td colspan="6" class="empty">Loading…</td></tr>`;
    try {
        const jobs = await api('/jobs/available');
        if (!jobs || jobs.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="empty">No available jobs right now. Check back soon.</td></tr>`;
            return;
        }
        tbody.innerHTML = jobs.map(j => `
      <tr>
        <td><strong>${x(j.title)}</strong>
          ${j.description ? `<div class="row-desc">${x(j.description)}</div>` : ''}
        </td>
        <td>${x(j.category || '—')}</td>
        <td>${x(j.location || '—')}</td>
        <td>${x(j.payRate || '—')}</td>
        <td>${x(j.postedBy)}</td>
        <td>
          <button class="btn-accept" onclick="acceptJob(${j.id}, this)">Accept</button>
        </td>
      </tr>`).join('');
    } catch {
        tbody.innerHTML = `<tr><td colspan="6" class="empty">Failed to load jobs</td></tr>`;
    }
}

// ── ACCEPT A JOB ─────────────────────────────────────────────
async function acceptJob(id, btn) {
    btn.disabled = true;
    btn.textContent = 'Accepting…';
    try {
        const res = await api(`/jobs/${id}/accept?worker=${encodeURIComponent(USERNAME)}`, 'PUT');
        toast(res.message || 'Job accepted!');
        loadAvailable();
        loadAccepted();
    } catch (err) {
        toast(err.message || 'Could not accept job');
        btn.disabled = false;
        btn.textContent = 'Accept';
    }
}

// ── MY ACCEPTED JOBS ──────────────────────────────────────────
async function loadAccepted() {
    const tbody = document.getElementById('accepted-tbody');
    tbody.innerHTML = `<tr><td colspan="5" class="empty">Loading…</td></tr>`;
    try {
        const jobs = await api(`/jobs/accepted/${encodeURIComponent(USERNAME)}`);
        if (!jobs || jobs.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="empty">You haven't accepted any jobs yet</td></tr>`;
            return;
        }
        tbody.innerHTML = jobs.map(j => `
      <tr>
        <td><strong>${x(j.title)}</strong></td>
        <td>${x(j.category || '—')}</td>
        <td>${x(j.location || '—')}</td>
        <td>${x(j.payRate || '—')}</td>
        <td>${x(j.postedBy)}</td>
      </tr>`).join('');
    } catch {
        tbody.innerHTML = `<tr><td colspan="5" class="empty">Failed to load</td></tr>`;
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
