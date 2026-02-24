<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Job Portal – Login / Register</title>
        <link rel="stylesheet" href="style.css" />
    </head>

    <body class="login-bg">

        <div class="login-card">
            <h1>Job Portal</h1>

            <!-- Tab toggle -->
            <div class="auth-tabs">
                <button class="tab-btn active" onclick="showTab('login')">Login</button>
                <button class="tab-btn" onclick="showTab('register')">Register</button>
            </div>

            <!-- Login form -->
            <form id="login-form" onsubmit="handleLogin(event)">
                <label>
                    Username
                    <input type="text" id="login-username" placeholder="e.g. john_doe" required autofocus />
                </label>
                <label>
                    Password
                    <input type="password" id="login-password" placeholder="Enter password" required />
                </label>
                <button type="submit" class="btn-primary full">Login</button>
            </form>

            <!-- Register form (hidden by default) -->
            <form id="register-form" style="display:none" onsubmit="handleRegister(event)">
                <label>
                    Username
                    <input type="text" id="reg-username" placeholder="e.g. john_doe" required />
                </label>
                <label>
                    Password
                    <input type="password" id="reg-password" placeholder="Choose a password" required />
                </label>
                <label>
                    Full Name
                    <input type="text" id="reg-fullname" placeholder="e.g. John Doe" required />
                </label>
                <label>
                    Email
                    <input type="email" id="reg-email" placeholder="e.g. john@example.com" required />
                </label>
                <label>
                    Role
                    <select id="reg-role">
                        <option value="USER">USER - I want to post jobs</option>
                        <option value="WORKER">WORKER - I want to accept jobs</option>
                    </select>
                </label>
                <button type="submit" class="btn-primary full">Register</button>
            </form>

            <p id="auth-error" class="auth-error"></p>
        </div>

        <script>
            var API = '<%= request.getContextPath() %>/api';

            // Redirect if already logged in
            if (sessionStorage.getItem('jwt')) {
                var role = sessionStorage.getItem('role');
                window.location.href = role === 'WORKER' ? 'worker.jsp' : 'user.jsp';
            }

            function showTab(tab) {
                document.getElementById('login-form').style.display = tab === 'login' ? '' : 'none';
                document.getElementById('register-form').style.display = tab === 'register' ? '' : 'none';
                document.querySelectorAll('.auth-tabs .tab-btn').forEach(function(b, i) {
                    b.classList.toggle('active', (tab === 'login' && i === 0) || (tab === 'register' && i === 1));
                });
                document.getElementById('auth-error').textContent = '';
            }

            async function handleLogin(e) {
                e.preventDefault();
                var errEl = document.getElementById('auth-error');
                errEl.textContent = '';
                try {
                    var res = await fetch(API + '/auth/login', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            username: document.getElementById('login-username').value.trim(),
                            password: document.getElementById('login-password').value
                        })
                    });
                    var data = await res.json();
                    if (!res.ok) { errEl.textContent = data.message || 'Login failed'; return; }
                    sessionStorage.setItem('jwt', data.token);
                    sessionStorage.setItem('username', data.username);
                    sessionStorage.setItem('role', data.role);
                    console.log('[Auth] Login success:', data.username, data.role);
                    window.location.href = data.role === 'WORKER' ? 'worker.jsp' : 'user.jsp';
                } catch (err) {
                    console.error('[Auth] Login error:', err);
                    errEl.textContent = 'Network error';
                }
            }

            async function handleRegister(e) {
                e.preventDefault();
                var errEl = document.getElementById('auth-error');
                errEl.textContent = '';
                try {
                    var res = await fetch(API + '/auth/register', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            username: document.getElementById('reg-username').value.trim(),
                            password: document.getElementById('reg-password').value,
                            fullName: document.getElementById('reg-fullname').value.trim(),
                            email: document.getElementById('reg-email').value.trim(),
                            role: document.getElementById('reg-role').value
                        })
                    });
                    var data = await res.json();
                    if (!res.ok) { errEl.textContent = data.message || 'Registration failed'; return; }
                    sessionStorage.setItem('jwt', data.token);
                    sessionStorage.setItem('username', data.username);
                    sessionStorage.setItem('role', data.role);
                    console.log('[Auth] Register success:', data.username, data.role);
                    window.location.href = data.role === 'WORKER' ? 'worker.jsp' : 'user.jsp';
                } catch (err) {
                    console.error('[Auth] Register error:', err);
                    errEl.textContent = 'Network error';
                }
            }
        </script>

    </body>

    </html>