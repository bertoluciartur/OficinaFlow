package oficinaflow.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Login único de demonstração (usuário e senha em application.properties).
 * shortcut: sem contas nem perfis; trocar por Spring Security com usuários no banco para uso real.
 */
@Controller
public class LoginController {

    static final String SESSAO = "usuario";

    @Value("${oficinaflow.login.usuario}")
    private String usuarioValido;

    @Value("${oficinaflow.login.senha}")
    private String senhaValida;

    @GetMapping("/login")
    public String formulario() {
        return "login";
    }

    @PostMapping("/login")
    public String entrar(@RequestParam String usuario, @RequestParam String senha,
                         HttpServletRequest req, RedirectAttributes ra) {
        if (!usuarioValido.equals(usuario.trim()) || !senhaValida.equals(senha)) {
            ra.addFlashAttribute("erro", "Usuário ou senha inválidos.");
            return "redirect:/login";
        }
        HttpSession sessao = req.getSession();
        req.changeSessionId();
        sessao.setAttribute(SESSAO, usuarioValido);
        return "redirect:/os";
    }

    @PostMapping("/logout")
    public String sair(HttpSession sessao) {
        sessao.invalidate();
        return "redirect:/login";
    }
}
