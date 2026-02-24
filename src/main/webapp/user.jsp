<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <% String username=(String) session.getAttribute("username"); String role=(String) session.getAttribute("role"); if
        (username==null || !"USER".equals(role)) { response.sendRedirect("index.jsp"); return; } %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Job Portal – Post Jobs</title>
            <link rel="stylesheet" href="style.css" />
        </head>

        <body>

            <header>
                <span class="brand">Job Portal</span>
                <nav>
                    <span class="nav-label">Posting as: <strong>
                            <%= username %>
                        </strong></span>
                    <span class="badge-USER">USER</span>
                </nav>
                <a href="logout" class="btn-logout">Logout</a>
            </header>

            <main>
                <div class="two-col">

                    <!-- POST JOB FORM -->
                    <div class="card">
                        <h2>Post a New Job</h2>
                        <form id="job-form" onsubmit="submitJob(event)">
                            <label>Job Title *
                                <input type="text" id="f-title" placeholder="e.g. Warehouse Picker" required />
                            </label>
                            <label>Category
                                <select id="f-category">
                                    <option value="">-- Select --</option>
                                    <option>Delivery</option>
                                    <option>Warehouse</option>
                                    <option>Cleaning</option>
                                    <option>Construction</option>
                                    <option>IT</option>
                                    <option>Admin</option>
                                    <option>Retail</option>
                                    <option>Other</option>
                                </select>
                            </label>
                            <label>Location
                                <input type="text" id="f-location" placeholder="e.g. Toronto, ON" />
                            </label>
                            <label>Pay Rate
                                <input type="text" id="f-pay" placeholder="e.g. $20/hr" />
                            </label>
                            <label>Description *
                                <textarea id="f-desc" rows="4" placeholder="Describe duties, hours, requirements..."
                                    required></textarea>
                            </label>
                            <button type="submit" class="btn-primary full">Post Job</button>
                        </form>
                    </div>

                    <!-- MY POSTED JOBS -->
                    <div class="card">
                        <div class="card-head">
                            <h2>My Posted Jobs</h2>
                            <button class="btn-refresh" onclick="loadMyJobs()">Refresh</button>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Status</th>
                                    <th>Accepted By</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="my-jobs-tbody">
                                <tr>
                                    <td colspan="4" class="empty">Loading...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                </div>
            </main>

            <!-- EDIT MODAL -->
            <div class="overlay" id="edit-modal">
                <div class="modal">
                    <div class="modal-head">
                        <h3>Edit Job</h3>
                        <button onclick="closeEditModal()">X</button>
                    </div>
                    <input type="hidden" id="edit-id" />
                    <label>Title *<input type="text" id="edit-title" /></label>
                    <label>Category
                        <select id="edit-category">
                            <option value="">-- Select --</option>
                            <option>Delivery</option>
                            <option>Warehouse</option>
                            <option>Cleaning</option>
                            <option>Construction</option>
                            <option>IT</option>
                            <option>Admin</option>
                            <option>Retail</option>
                            <option>Other</option>
                        </select>
                    </label>
                    <label>Location<input type="text" id="edit-location" /></label>
                    <label>Pay Rate<input type="text" id="edit-pay" /></label>
                    <label>Description *<textarea id="edit-desc" rows="4"></textarea></label>
                    <div class="modal-foot">
                        <button onclick="closeEditModal()">Cancel</button>
                        <button class="btn-primary" onclick="saveEdit()">Save</button>
                    </div>
                </div>
            </div>

            <div id="toast"></div>

            <script>
                var API = '<%= request.getContextPath() %>/api';
                var USERNAME = '<%= username %>';
            </script>
            <script src="user.js"></script>
        </body>

        </html>