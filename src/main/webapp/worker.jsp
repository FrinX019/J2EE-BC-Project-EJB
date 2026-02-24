<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Job Portal – Find Jobs</title>
        <link rel="stylesheet" href="style.css" />
    </head>

    <body>

        <header>
            <span class="brand">Job Portal</span>
            <nav>
                <span class="nav-label">Working as: <strong id="display-username"></strong></span>
                <span class="badge-WORKER">WORKER</span>
            </nav>
            <a href="#" class="btn-logout" onclick="logout()">Logout</a>
        </header>

        <main>

            <!-- AVAILABLE JOBS -->
            <div class="card">
                <div class="card-head">
                    <h2>Available Jobs</h2>
                    <button class="btn-refresh" onclick="loadAvailable()">Refresh</button>
                </div>
                <p class="hint">Click <strong>Accept</strong> to pick up a job. Once accepted it is yours -- first
                    come, first served.</p>
                <table>
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Pay</th>
                            <th>Posted By</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="avail-tbody">
                        <tr>
                            <td colspan="6" class="empty">Loading...</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- MY ACCEPTED JOBS -->
            <div class="card" style="margin-top:20px">
                <div class="card-head">
                    <h2>Jobs I Have Accepted</h2>
                    <button class="btn-refresh" onclick="loadAccepted()">Refresh</button>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Pay</th>
                            <th>Posted By</th>
                        </tr>
                    </thead>
                    <tbody id="accepted-tbody">
                        <tr>
                            <td colspan="5" class="empty">None yet</td>
                        </tr>
                    </tbody>
                </table>
            </div>

        </main>

        <div id="toast"></div>

        <script>
            // JWT-based auth guard
            var jwt = sessionStorage.getItem('jwt');
            var USERNAME = sessionStorage.getItem('username');
            var role = sessionStorage.getItem('role');
            if (!jwt || role !== 'WORKER') {
                sessionStorage.clear();
                window.location.href = 'index.jsp';
            }
            document.getElementById('display-username').textContent = USERNAME;

            var API = '<%= request.getContextPath() %>/api';

            function logout() {
                sessionStorage.clear();
                window.location.href = 'index.jsp';
            }
        </script>
        <script src="worker.js"></script>
    </body>

    </html>