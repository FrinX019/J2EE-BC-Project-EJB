<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Job Portal – Login</title>
        <link rel="stylesheet" href="style.css" />
    </head>

    <body class="login-bg">

        <div class="login-card">
            <h1>Job Portal</h1>
            <p class="login-sub">Enter your username and select your role to continue</p>

            <form action="login" method="post">
                <label>
                    Username
                    <input type="text" name="username" placeholder="e.g. john_doe" required autofocus />
                </label>
                <label>
                    Role
                    <select name="role">
                        <option value="USER">USER – I want to post jobs</option>
                        <option value="WORKER">WORKER – I want to accept jobs</option>
                    </select>
                </label>
                <button type="submit" class="btn-primary full">Enter Portal</button>
            </form>
        </div>

    </body>

    </html>