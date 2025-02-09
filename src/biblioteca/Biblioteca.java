package biblioteca;
import perfis.Administrador;
import perfis.Leitor;
import perfis.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

import static perfis.Administrador.*;
import static perfis.Leitor.*;


public class Biblioteca implements  Serializable {
    private static ArrayList<Livro> livros = new ArrayList<>();
    private static ArrayList<Leitor> leitores = new ArrayList<>();
    private static ArrayList<Administrador> administradores = new ArrayList<>();
    protected static final String chaveAdministrador = "12345";

    public static void setLivros(Livro novoLivro) {
        livros.add(novoLivro);
    }

    public static ArrayList<Leitor> getLeitores() {
        return leitores;
    }

    public static ArrayList<Administrador> getAdministradores() {
        return administradores;
    }


    private static final Random random = new Random();

    public static Usuario criarUsuario(String nome, String senha, String email, boolean tipo, boolean log, String id) {
        if (tipo) {
            return new Administrador(nome, senha, email, tipo, log, id);
        } else {
            return new Leitor(nome, senha, email, tipo,log,0, id);
        }
    }


    public static void cadastrarAdmin() {
        String chave = JOptionPane.showInputDialog("Digite a chave de administrador: ");

        if ("12345".equals(chave)) {
            JOptionPane.showMessageDialog(null, "Chave válida!");

            String nome = JOptionPane.showInputDialog("Digite seu nome: ");
            String senha = JOptionPane.showInputDialog("Digite sua senha: ");
            String email = JOptionPane.showInputDialog("Digite seu email: ");

            if (nome == null || nome.trim().isEmpty() ||
                    senha == null || senha.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    !isValidEmail(email)) {
                JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos corretamente. Email válido é necessário.");
                return;
            }

            for (Administrador admin : getAdministradores()) {
                if (admin.getNome().equalsIgnoreCase(nome)) {
                    JOptionPane.showMessageDialog(null, "Já existe um administrador com este nome.");
                    return;
                }
            }

            Administrador novoAdmin = (Administrador) criarUsuario(nome, senha, email, true, false, "123" + nome);
            getAdministradores().add(novoAdmin);
            salvarPerfisAdmin();
            JOptionPane.showMessageDialog(null, "Novo administrador cadastrado com sucesso. ID: " + novoAdmin.getId());
        } else {
            JOptionPane.showMessageDialog(null, "Chave inválida!");
        }
    }

public static void cadastrarNovoLeitor() {
    String nome = JOptionPane.showInputDialog("Digite seu nome: ");
    String senha = JOptionPane.showInputDialog("Digite sua senha: ");
    String email = JOptionPane.showInputDialog("Digite seu email: ");

    if (nome == null || nome.trim().isEmpty() ||
            senha == null || senha.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            !isValidEmail(email)) {
        JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos corretamente. Email válido é necessário.");
        return;
    }

    for (Leitor leitor : leitores) {
        if (leitor.getNome().equalsIgnoreCase(nome)) {
            JOptionPane.showMessageDialog(null, "Já existe um usuário com este nome.");
            return;
        }
    }

    Leitor novoLeitor = (Leitor) criarUsuario(nome, senha, email, false, false, "111" + nome);
    leitores.add(novoLeitor);
    salvarPerfisLeitor();
    JOptionPane.showMessageDialog(null, "Novo usuário cadastrado com sucesso. ID: " + ((Leitor) novoLeitor).getId());
}

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && Pattern.matches(emailRegex, email);
    }

    public static Usuario loginUsuario() {
        if (new File("cadastrosLeitor.dat").exists()) {
            carregarPerfisLeitor();
        }

        if (new File("cadastrosAdms.dat").exists()) {
            carregarPerfisAdmin();
        }

        boolean logged = false;
        Usuario usuarioAutenticado = null;

        JOptionPane.showMessageDialog(null,
                "Dicas para o formato do ID:\n" +
                        "Admin: 123NomeAdmin\n" +
                        "Leitor: 111NomeLeitor",
                "Formato do ID",
                JOptionPane.INFORMATION_MESSAGE);

        String id = JOptionPane.showInputDialog(null, "Digite seu ID: ");
        String senha = JOptionPane.showInputDialog(null, "Digite sua senha: ");

        for (Administrador admin : administradores) {
            if (admin.getId() != null && admin.getId().equals(id) &&
                    admin.getSenha() != null && admin.getSenha().equals(senha)) {
                logged = true;
                usuarioAutenticado = admin;
                break;
            }
        }

        if (!logged) {
            for (Leitor leitor : leitores) {
                if (leitor.getId() != null && leitor.getId().equals(id) &&
                        leitor.getSenha() != null && leitor.getSenha().equals(senha)) {
                    logged = true;
                    usuarioAutenticado = leitor;
                    break;
                }
            }
        }

        if (logged) {
            usuarioAutenticado.setLogged(true);
            JOptionPane.showMessageDialog(null, "Acesso autenticado com sucesso.");
            JOptionPane.showMessageDialog(null, "Bem-vindo, " + usuarioAutenticado.getNome() + "!");
            return usuarioAutenticado;
        } else {
            JOptionPane.showMessageDialog(null, "ID ou senha incorretos. Por favor, verifique novamente.");
            usuarioAutenticado = new Usuario();
            return usuarioAutenticado;
        }
    }

    public static void notificarLivrosRecentes() {
        carregarLivros();
        if(livros.isEmpty()){
            return;
        }
        LocalDate hoje = LocalDate.now();
        LocalDate duasSemanasAtras = hoje.minus(2, ChronoUnit.WEEKS);

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Novos Livros Adicionados na Biblioteca:\n");

        boolean encontrouLivrosRecentes = false;
        for (Livro livro : livros) {
            if (livro.getDataAdicao() != null && livro.getDataAdicao().isAfter(duasSemanasAtras)) {
                encontrouLivrosRecentes = true;
                mensagem.append("Título: ").append(livro.getTitulo()).append("\n")
                        .append("Categoria: ").append(livro.getCategoria()).append("\n\n");
            }
        }

        if (!encontrouLivrosRecentes) {
            mensagem.append("Nenhum livro novo foi adicionado nas últimas duas semanas.");
        }

        // Exibe uma caixa de diálogo modal obrigatória para todos os leitores
        JOptionPane.showMessageDialog(null, mensagem.toString(), "Notificação de Livros Novos", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void verificarNotificar(Usuario leitor) {
        if (!(leitor instanceof Leitor)) {
            throw new IllegalArgumentException("O usuário fornecido não é um leitor.");
        }

        Leitor leitorCast = (Leitor) leitor;

        // Verifica se o leitor deve ser notificado
        if (leitorCast.isShouldNotify()) {
            JOptionPane.showMessageDialog(null, "Você tem pendências com livros emprestados. Por favor, regularize sua situação.");
        }

        // Verifica se algum livro emprestado está próximo da data de vencimento
        if (leitorCast.getLivrosEmprestados() != null) {
            LocalDate hoje = LocalDate.now();
            LocalDate umaSemanaAtras = hoje.plusWeeks(1);

            for (LivroEmprestado livroEmprestado : leitorCast.getLivrosEmprestados()) {
                if (livroEmprestado.getDataVencimento().isBefore(umaSemanaAtras)) {
                    JOptionPane.showMessageDialog(null, "O prazo para a devolução do livro \"" + livroEmprestado.getLivro().getTitulo() + "\" está se aproximando. Data de vencimento: " + livroEmprestado.getDataVencimento() + ". Por favor, devolva-o o quanto antes.");
                    break; // Notifica apenas uma vez por período
                }
            }
        }
    }

    private static Timer timer = new Timer();

    public static void iniciarRecomendacoes(Usuario leitor) {
        if (leitor == null) {
            JOptionPane.showMessageDialog(null, "Leitor não autenticado.");
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                recomendarLivroSutil(leitor);
            }
        }, 20000);
    }

    private static void recomendarLivroSutil(Usuario usuario) {
        Leitor leitor = (Leitor) usuario;
        if (leitor == null || leitor.getLogLivros() == null || leitor.getLogLivros().isEmpty()) {
            return;
        }

        Set<String> categorias = new HashSet<>();
        Set<String> autores = new HashSet<>();

        for (LogLivro log : leitor.getLogLivros()) {
            if (!log.isDevolved() && log.getLivro() != null) {
                Livro livro = log.getLivro();
                categorias.add(livro.getCategoria());
                autores.add(livro.getAutor());
            }
        }

        Livro livroRecomendado = getLivros().stream()
                .filter(livro -> !leitor.getLogLivros().stream()
                        .anyMatch(log -> log.getLivro().getISBN() == livro.getISBN() && !log.isDevolved()))
                .filter(livro -> categorias.contains(livro.getCategoria()) || autores.contains(livro.getAutor()))
                .findFirst()
                .orElse(null);

        if (livroRecomendado != null) {
            JOptionPane.showMessageDialog(null,
                    "Baseado no seu histórico de solicitações, você pode gostar de:\n\n" +
                            "Título: " + livroRecomendado.getTitulo() + "\n" +
                            "Autor: " + livroRecomendado.getAutor() + "\n" +
                            "Categoria: " + livroRecomendado.getCategoria(),
                    "Recomendação de Livro",
                    JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null,"Sem livros a recomendar");
        }
    }

    public static ArrayList<Livro> getLivros() {
        return livros;
    }

    public static void gerirUsuarios() {
        String[] opcoes = {"Gerenciar Leitores", "Gerenciar Administradores", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null,
                "Escolha a opção de gerenciamento:",
                "Gerenciar Usuários",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        switch (escolha) {
            case 0:
                gerenciarLeitores();
                break;
            case 1:
                gerenciarAdministradores();
                break;
            case 2:
                return;
            default:
                JOptionPane.showMessageDialog(null, "Opção inválida.");
                break;
        }
    }

    private static void gerenciarLeitores() {
        if (leitores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum leitor cadastrado.");
            return;
        }

        String[] opcoesLeitores = leitores.stream()
                .map(leitor -> leitor.getNome() + " - " + leitor.getId())
                .toArray(String[]::new);

        String leitorEscolhido = (String) JOptionPane.showInputDialog(
                null,
                "Selecione o leitor para gerenciar:",
                "Gerenciar Leitor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesLeitores,
                opcoesLeitores[0]
        );

        if (leitorEscolhido != null) {
            String idLeitorEscolhido = leitorEscolhido.split(" - ")[1];
            Leitor leitor = leitores.stream()
                    .filter(l -> l.getId().equals(idLeitorEscolhido))
                    .findFirst()
                    .orElse(null);

            if (leitor != null) {
                alterarInformacoesLeitor(leitor);
            } else {
                JOptionPane.showMessageDialog(null, "Leitor não encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum leitor selecionado.");
        }
    }

    private static void gerenciarAdministradores() {
        if (administradores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum administrador cadastrado.");
            return;
        }

        String[] opcoesAdmins = administradores.stream()
                .map(admin -> admin.getNome() + " - " + admin.getId())
                .toArray(String[]::new);

        String adminEscolhido = (String) JOptionPane.showInputDialog(
                null,
                "Selecione o administrador para gerenciar:",
                "Gerenciar Administrador",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesAdmins,
                opcoesAdmins[0]
        );

        if (adminEscolhido != null) {
            String idAdminEscolhido = adminEscolhido.split(" - ")[1];
            Administrador admin = administradores.stream()
                    .filter(a -> a.getId().equals(idAdminEscolhido))
                    .findFirst()
                    .orElse(null);

            if (admin != null) {
                alterarInformacoesAdmin(admin);
            } else {
                JOptionPane.showMessageDialog(null, "Administrador não encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum administrador selecionado.");
        }
    }

    private static void alterarInformacoesLeitor(Leitor leitor) {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(5, 2));

        JTextField nomeField = new JTextField(leitor.getNome());
        JPasswordField senhaField = new JPasswordField(leitor.getSenha());
        JTextField emailField = new JTextField(leitor.getEmail());
        JCheckBox penalidadeCheckBox = new JCheckBox("Marcar como inadimplente", leitor.isShouldNotify());

        painel.add(new JLabel("Nome:"));
        painel.add(nomeField);
        painel.add(new JLabel("Senha:"));
        painel.add(senhaField);
        painel.add(new JLabel("Email:"));
        painel.add(emailField);
        painel.add(new JLabel("Penalidade:"));
        painel.add(penalidadeCheckBox);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Alterar Informações do Leitor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            leitor.setNome(nomeField.getText());
            leitor.setSenha(new String(senhaField.getPassword()));
            leitor.setEmail(emailField.getText());
            leitor.setShouldNotify(penalidadeCheckBox.isSelected());

            salvarPerfisLeitor();
            JOptionPane.showMessageDialog(null, "Informações do leitor alteradas com sucesso.");
        }
    }

    private static void alterarInformacoesAdmin(Administrador admin) {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(3, 2));

        JTextField nomeField = new JTextField(admin.getNome());
        JPasswordField senhaField = new JPasswordField(admin.getSenha());
        JTextField emailField = new JTextField(admin.getEmail());

        painel.add(new JLabel("Nome:"));
        painel.add(nomeField);
        painel.add(new JLabel("Senha:"));
        painel.add(senhaField);
        painel.add(new JLabel("Email:"));
        painel.add(emailField);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Alterar Informações do Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            admin.setNome(nomeField.getText());
            admin.setSenha(new String(senhaField.getPassword()));
            admin.setEmail(emailField.getText());

            salvarPerfisAdmin();
            JOptionPane.showMessageDialog(null, "Informações do administrador alteradas com sucesso.");
        }
    }

    public static void menuLeitor(Usuario usuario) {
            verificarNotificar(usuario);
            notificarLivrosRecentes();
            iniciarRecomendacoes(usuario);

            int option = 0;

            while (option != 7){

                String[] options = {"Exibir Livros", "Top Livros", "Buscar Livro", "Solicitar Empréstimo", "Devolver Livro", "Livros Emprestados", "Log", "Voltar"};
                option = JOptionPane.showOptionDialog(null,
                        "Escolha uma opção:",
                        "Menu Leitor",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]);

                switch (option) {
                    case 0:
                        exibirLivros();
                        break;
                    case 1:
                        exibirLivrosMaisSolicitados();
                        break;
                    case 2:
                        buscaLivro();
                        break;
                    case 3:
                        solicitarEmprestimo(usuario);
                        break;
                    case 4:
                        devolverLivro(usuario);
                        break;
                    case 5:
                        logEmprestimos(usuario);
                        break;
                    case 6:
                        verLogs();
                        break;
                    case 7:
                        return;
                    default:
                        JOptionPane.showMessageDialog(null, "Opção inválida.");
                        break;

                }
            }

    }

    public static void menuAdmin() {

        int option = 0;

        while (option != 8){
        String[] options = {"Cadastrar Livro", "Buscar Livro", "Alterar Livro","Remover Livro","Exibir Livros", "Emprestar Livro", "Estatistis da Biblioteca","Gerir Usuarios","Voltar"};
        option = JOptionPane.showOptionDialog(null,
                "Escolha uma opção:",
                "Menu Administrador",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (option) {
            case 0:
                cadastraLivro();
                break;
            case 1:
                buscaLivro();
                break;
            case 2:
                alteraLivro();
                break;
            case 3:
                removerLivro();
                break;
            case 4:
                exibirLivros();
                break;
            case 5:
                emprestarLivro();
                break;
            case 6:
                estatisticasBiblioteca();
                break;
            case 7:
                gerirUsuarios();
                break;
            case 8:
                return;
            default:
                JOptionPane.showMessageDialog(null, "Opção inválida.");
                break;
        }

        }
    }

    public static Set<Integer> getIsbnExistentes() {
        return isbnExistentes;
    }

    public static void salvarPerfisAdmin() {
        File arquivo = new File("cadastrosAdms.dat");

        File diretorioPai = arquivo.getParentFile();
        if (diretorioPai != null && !diretorioPai.exists()) {
            diretorioPai.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            for (Administrador admin : administradores) {
                oos.writeObject(admin);
            }
            JOptionPane.showMessageDialog(null, "Perfis de administradores salvos com sucesso.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar perfis de administradores: " + e.getMessage());
        }
    }

    public static void salvarPerfisLeitor() {
        // Define o caminho para o arquivo
        File arquivo = new File("cadastrosLeitor.dat");

        // Verifica se o diretório pai existe e cria se não existir
        File diretorioPai = arquivo.getParentFile();
        if (diretorioPai != null && !diretorioPai.exists()) {
            diretorioPai.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            for (Leitor leitor : leitores) {
                oos.writeObject(leitor);
            }
            JOptionPane.showMessageDialog(null, "Perfis de leitores salvos com sucesso.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar perfis de leitores: " + e.getMessage());
        }
    }

    public static void salvarLivros() {
        // Define o caminho para o arquivo
        File arquivo = new File("cadastrosLivro.dat");

        // Verifica se o diretório pai existe e cria se não existir
        File diretorioPai = arquivo.getParentFile();
        if (diretorioPai != null && !diretorioPai.exists()) {
            diretorioPai.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            for (Livro livro : livros) {
                oos.writeObject(livro);
            }
            JOptionPane.showMessageDialog(null, "Livros salvos com sucesso.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar livros: " + e.getMessage());
        }
    }

    private static Set <String> idsAdminsExistentes = new HashSet<>();

    public static void carregarPerfisAdmin() {
        File arquivo = new File("cadastrosAdms.dat");
        if (!arquivo.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            while (true) {
                try {
                    Administrador admin = (Administrador) ois.readObject();

                    if (idsAdminsExistentes.contains(admin.getId())) {
                        continue;
                    }

                    administradores.add(admin);
                    idsAdminsExistentes.add(admin.getId());

                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Classe não encontrada: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar administradores: " + e.getMessage());
        }
    }

    private static Set<String> idsLeitoresExistentes = new HashSet<>();

    public static void carregarPerfisLeitor() {
        File arquivo = new File("cadastrosLeitor.dat");
        if (!arquivo.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            while (true) {
                try {
                    Leitor leitor = (Leitor) ois.readObject();

                    if (idsLeitoresExistentes.contains(leitor.getId())) {
                        continue;
                    }

                    if (leitor.getLivrosEmprestados() == null) {
                        leitor.setLivrosEmprestados(new ArrayList<>());
                    }

                    verificarPenalidadeEAtualizar(leitor);

                    leitores.add(leitor);
                    idsLeitoresExistentes.add(leitor.getId());
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Classe não encontrada: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar leitores: " + e.getMessage());
        }
    }

    private static void verificarPenalidadeEAtualizar(Leitor leitor) {
        boolean hasPenalty = false;
        for (LivroEmprestado livroEmprestado : leitor.getLivrosEmprestados()) {
            if (livroEmprestado.getDataVencimento().isBefore(LocalDate.now())) {
                hasPenalty = true;
                break;
            }
        }

        leitor.setPenalty(hasPenalty);
        leitor.setShouldNotify(hasPenalty);
    }

    private static Set<Integer> isbnExistentes = new HashSet<>();

    public static void carregarLivros() {
        File arquivo = new File("cadastrosLivro.dat");
        if (!arquivo.exists()) {
            return;
        }

        isbnExistentes.clear();
        for (Livro livro : livros) {
            isbnExistentes.add(livro.getISBN());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            while (true) {
                try {
                    Livro livro = (Livro) ois.readObject();

                    if (!isbnExistentes.contains(livro.getISBN())) {
                        if (livro.getDataAdicao() != null) {
                            LocalDate hoje = LocalDate.now();
                            long semanasDesdeAdicao = ChronoUnit.WEEKS.between(livro.getDataAdicao(), hoje);
                            if (semanasDesdeAdicao < 1) {
                                livro.setNew(true);
                            } else {
                                livro.setNew(false);
                            }
                        }

                        livros.add(livro);
                        isbnExistentes.add(livro.getISBN());
                    }
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Classe não encontrada: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar livros: " + e.getMessage());
        }
    }

}

