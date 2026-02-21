<div class="navbar bg-primary shadow-sm mb-8">
    <div class="navbar-start">
        <div class="dropdown">
            <div tabIndex="0" role="button" class="btn btn-ghost btn-circle">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block w-5 h-5 stroke-current">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
                </svg>
            </div>
            <ul tabIndex="0" class="menu menu-sm dropdown-content mt-3 z-1 p-2 shadow-sm bg-secondary text-secondary-content rounded-box w-52">
                <li>
                    <a href="${pageContext.request.contextPath}/app/secure/dashboard">
                        <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                            <use xlink:href="#home"></use>
                        </svg>
                        Dashboard
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/app/secure/admin">
                        <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                            <use xlink:href="#shield-exclamation"></use>
                        </svg>
                        Admin Zone
                    </a>
                </li>
                <li class="border-dotted border-t-4 border-[var(--a)]">
                    <a href="${pageContext.request.contextPath}/app/secure/logout">
                        <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                            <use xlink:href="#arrow-right-start-on-rectangle"></use>
                        </svg>
                        Logout
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <div class="navbar-center">
        <a href="${pageContext.servletContext.contextPath}/app/secure/dashboard" class="btn btn-ghost text-xl">Traqqr</a>
    </div>
</div>