package pseudointerface;
import perfis.Usuario;
import javax.swing.*;
import static biblioteca.Biblioteca.*;

public class TelaInicial {
    public static void main(String[] args) {

        JOptionPane.showMessageDialog(null, "Bem-vindo ao Sistema Gerenciamento da Biblioteca Digital Universitária!");

        int option;
        while (true) {
        String[] options = {"Cadastro", "Login", "Sair"};
        option = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Tela inicial",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            switch (option) {
                case 0:
                    String[] opts = {"Cadastro de Leitor", "Cadastro de Administrador"};
                    int opt = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Tela inicial",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opts, opts[0]);
                    if (opt == 0) {
                        cadastrarNovoLeitor();
                        break;
                    } else if (opt == 1) {
                        cadastrarAdmin();
                        break;
                    }else if(opt == 2){
                      break;
                    }
                    break;
                case 1:
                    Usuario usuario = loginUsuario();
                    if(usuario.isLogged()){
                    if(usuario.getPermissao()){
                     menuAdmin();
                    }else{
                     menuLeitor(usuario);
                    }
                    }else{
                        JOptionPane.showMessageDialog(null,"Usuário não encontrado!");
                    }
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Saindo...");
                    System.exit(0);
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Tente novamente.");
            }
        }
    }


}
