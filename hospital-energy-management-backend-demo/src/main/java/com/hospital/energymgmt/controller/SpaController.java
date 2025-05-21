package com.hospital.energymgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SpaController {

    /**
     * Forwards requests made to frontend routes (like /login, /dashboard, etc.)
     * to the main index.html page. This allows the SPA's router to handle them.
     * The regex matches paths that:
     * - Do not start with "api/", "v2/", "swagger-resources/", "swagger-ui/", "webjars/",
     *   "css/", "js/", "fonts/", "static/".
     * - Are not "favicon.ico".
     * - Do not contain a '.' (dot) in the last path segment (to avoid matching actual files).
     * It also handles the root path "/".
     */
    @RequestMapping(value = {"/", "/{path:^(?!api|v2|swagger-resources|swagger-ui|webjars|css|js|fonts|static|favicon.ico)[^.]*}/**"})
    public String forwardSpaRoutes(HttpServletRequest request) {
        // Log the path being forwarded for debugging if needed
        // System.out.println("Forwarding SPA route: " + request.getRequestURI());
        return "forward:/index.html";
    }
}
