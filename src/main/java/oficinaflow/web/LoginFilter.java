package oficinaflow.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Bloqueia qualquer página sem sessão iniciada; só a tela de login e o CSS ficam públicos. */
@Component
public class LoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        boolean logado = sessao != null && sessao.getAttribute(LoginController.SESSAO) != null;
        String caminho = req.getRequestURI();
        if (logado || caminho.equals("/login") || caminho.equals("/style.css")) {
            chain.doFilter(req, res);
        } else {
            res.sendRedirect("/login");
        }
    }
}
