package Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Funções {

    private BufferedImage originalImage;
    private BufferedImage canalR;
    private BufferedImage canalG;
    private BufferedImage canalB;
    private BufferedImage currentImage; 
    private JLabel editedLabel;
    private JLabel originalLabel;
    private List<String> usuariosRegistrados;
    private List<String> senhasRegistradas;
    private List<String> planosRegistrados;
    private boolean usuarioAutenticado;
    private boolean planoPremium;
    private JFrame telaInicial;
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Funções editor = new Funções();
            editor.showLoginScreen();
        });
    }
    private void showLoginScreen() {
        telaInicial = new JFrame("Tela Inicial");
        telaInicial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        telaInicial.setSize(300, 150);
        telaInicial.setLayout(new FlowLayout());

        JButton cadastrarButton = new JButton("Cadastrar");
        JButton logarButton = new JButton("Logar");

        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCadastroDialog();
            }
        });

        logarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginDialog();
            }
        });

        telaInicial.add(cadastrarButton);
        telaInicial.add(logarButton);

        telaInicial.setLocationRelativeTo(null);
        telaInicial.setVisible(true);
    }

    private void showCadastroDialog() {
        JFrame telaCadastro = new JFrame("Cadastro");
        telaCadastro.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        telaCadastro.setSize(300, 200);
        telaCadastro.setLayout(new FlowLayout());

        JTextField campoUsuario = new JTextField(20);
        JPasswordField campoSenha = new JPasswordField(20);
        JComboBox<String> comboPlanos = new JComboBox<>(new String[]{"Simples", "Premium"});
        JButton cadastrarButton = new JButton("Cadastrar");

        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String usuario = campoUsuario.getText();
                String senha = new String(campoSenha.getPassword());
                String plano = (String) comboPlanos.getSelectedItem();

                if (cadastrarUsuario(usuario, senha, plano)) {
                    JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Cadastro", JOptionPane.INFORMATION_MESSAGE);
                    telaCadastro.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        telaCadastro.add(new JLabel("Usuário:"));
        telaCadastro.add(campoUsuario);
        telaCadastro.add(new JLabel("Senha:"));
        telaCadastro.add(campoSenha);
        telaCadastro.add(new JLabel("Plano:"));
        telaCadastro.add(comboPlanos);
        telaCadastro.add(cadastrarButton);

        telaCadastro.setLocationRelativeTo(null);
        telaCadastro.setVisible(true);
    }

    private boolean cadastrarUsuario(String usuario, String senha, String plano) {
        if (usuariosRegistrados == null) {
            usuariosRegistrados = new ArrayList<>();
            senhasRegistradas = new ArrayList<>();
            planosRegistrados = new ArrayList<>();
        }
     
        if (usuariosRegistrados.contains(usuario)) {
            
            return false;
        }

        usuariosRegistrados.add(usuario);
        senhasRegistradas.add(senha);
        planosRegistrados.add(plano);

        return true;
    }
    private void showLoginDialog() {
        JFrame telaLogin = new JFrame("Login");
        telaLogin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        telaLogin.setSize(300, 150);
        telaLogin.setLayout(new FlowLayout());

        JTextField campoUsuario = new JTextField(20);
        JPasswordField campoSenha = new JPasswordField(20);
        JButton logarButton = new JButton("Logar");

        logarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarLogin(campoUsuario.getText(), new String(campoSenha.getPassword()), telaLogin);
            }
        });

        telaLogin.setLocationRelativeTo(null);
        telaLogin.setVisible(true);
    
        telaLogin.add(new JLabel("Usuário:"));
        telaLogin.add(campoUsuario);
        telaLogin.add(new JLabel("Senha:"));
        telaLogin.add(campoSenha);
        telaLogin.add(new JLabel("Plano:"));
        telaLogin.add(logarButton);

        telaLogin.setLocationRelativeTo(null);
        telaLogin.setVisible(true);
    }

    private void realizarLogin(String usuario, String senha, JFrame telaLogin) {
        if (usuarioAutenticado) {
            JOptionPane.showMessageDialog(null, "Você já está logado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planoAssociado = obterPlano(usuario, senha);

        if (planoAssociado != null) {
            usuarioAutenticado = true;
            planoPremium = "Premium".equals(planoAssociado);

            JOptionPane.showMessageDialog(null, "Login bem-sucedido! Plano: " + planoAssociado, "Login", JOptionPane.INFORMATION_MESSAGE);
            telaInicial.dispose();

            telaLogin.dispose();
            showImageEditor();
        } else {
            JOptionPane.showMessageDialog(null, "Usuário ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
    private String obterPlano(String usuario, String senha) {
        if (usuariosRegistrados != null && senhasRegistradas != null && planosRegistrados != null) {
            for (int i = 0; i < usuariosRegistrados.size(); i++) {
                String usuarioRegistrado = usuariosRegistrados.get(i);
                String senhaRegistrada = senhasRegistradas.get(i);
                String planoRegistrado = planosRegistrados.get(i);

                if (usuario.equals(usuarioRegistrado) && senha.equals(senhaRegistrada)) {
                    return planoRegistrado;
                }
            }
        }
        return null;
    }
    private void showImageEditor() {
    	
        frame = new JFrame("Editor de Imagens");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        originalLabel = new JLabel();
        mainPanel.add(originalLabel, BorderLayout.WEST);

       
        editedLabel = new JLabel();
        mainPanel.add(editedLabel, BorderLayout.EAST);


        frame.getContentPane().add(mainPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton loadImageButton = new JButton("Carregar Imagem");
        loadImageButton.addActionListener(e -> {
            try {
                loadAndShowImage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        buttonPanel.add(loadImageButton);
        
        JButton salvarImagemButton = new JButton("Salvar Imagem");
        buttonPanel.add(salvarImagemButton);

        
        salvarImagemButton.addActionListener(e -> {
            System.out.println("Botão Salvar Imagem pressionado");
            
            if (currentImage != null) {
                System.out.println("Imagem não é nula. Salvando...");
                salvarImagem2(currentImage, "ImagemAlterada");
            } else {
                System.out.println("Imagem é nula. Não é possível salvar.");
                JOptionPane.showMessageDialog(null, "Nenhuma imagem para salvar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton clarearButton = new JButton("Clarear Imagem");
        clarearButton.addActionListener(e -> {
            int valorClareamento = obterValor("Digite o valor de clareamento (0 a 100):");
            currentImage = clarearImagem(currentImage, valorClareamento);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(clarearButton);

        JButton escurecerButton = new JButton("Escurecer Imagem");
        escurecerButton.addActionListener(e -> {
            int valorEscurecimento = obterValor("Digite o valor de escurecimento (0 a 100):");
            currentImage = escurecerImagem(currentImage, valorEscurecimento);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(escurecerButton);

        JButton filtroLaplacianoButton = new JButton("Filtro Laplaciano");
        filtroLaplacianoButton.addActionListener(e -> {
            currentImage = aplicarFiltroLaplaciano(currentImage);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(filtroLaplacianoButton);

        JButton filtroMedianaButton = new JButton("Filtro de Mediana");
        filtroMedianaButton.addActionListener(e -> {
            int tamanhoFiltro = obterValor("Digite o tamanho do filtro de mediana (ímpar, ex: 3, 5, 7):");
            currentImage = aplicarFiltroMediana(currentImage, tamanhoFiltro);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(filtroMedianaButton);

        JButton rotacionarButton = new JButton("Rotacionar Imagem");
        rotacionarButton.addActionListener(e -> {
            int grausRotacao = obterValor("Digite o valor de rotação (90, 180 ou 270):");
            currentImage = rotacionarImagem(currentImage, grausRotacao);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(rotacionarButton);

        JButton espelharButton = new JButton("Espelhar Imagem");
        espelharButton.addActionListener(e -> {
            String direcaoEspelhamento = obterDirecaoEspelhamento();
            currentImage = espelharImagem(currentImage, direcaoEspelhamento);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(espelharButton);

        JButton extrairRGBButton = new JButton("Extrair Canais RGB");
        extrairRGBButton.addActionListener(e -> {
            extrairRGB(originalImage);
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(extrairRGBButton);

        JButton unirRGBButton = new JButton("Unir Canais RGB");
        unirRGBButton.addActionListener(e -> {
            selecionarImagensParaUnirRGB();
            unirRGB();
            editedLabel.setIcon(new ImageIcon(currentImage)); 
        });
        buttonPanel.add(unirRGBButton);

        JButton exibirImagemButton = new JButton("Exibir Imagem");
        exibirImagemButton.addActionListener(e -> {
            exibirImagem();
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(exibirImagemButton);
        
        JButton Logout = new JButton("Logout");
        Logout.addActionListener(e -> {
            Logout();
            editedLabel.setIcon(new ImageIcon(currentImage));
        });
        buttonPanel.add(Logout);


        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void Logout() {
        usuarioAutenticado = false;
        planoPremium = false;

        frame.dispose();

        showLoginScreen();
    }
    private int obterValor(String mensagem) {
        String input = JOptionPane.showInputDialog(null, mensagem);
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private void loadAndShowImage() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione a imagem");
        int escolha = fileChooser.showOpenDialog(null);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            loadImage(fileChooser.getSelectedFile().getAbsolutePath());

            originalLabel.setIcon(new ImageIcon(originalImage));

            currentImage = deepCopy(originalImage);

            editedLabel.setIcon(new ImageIcon(currentImage));

                    }
    }
    private String obterDirecaoEspelhamento() {
        Object[] opcoes = {"Horizontal", "Vertical"};
        int escolha = JOptionPane.showOptionDialog(null, "Escolha a direção do espelhamento:", "Espelhar Imagem",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) {
            return "H";
        } else {
            return "V";
        }
    }

    private void exibirImagem() {
        JFrame frame = new JFrame("Imagem Exibida");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JLabel label = new JLabel(new ImageIcon(currentImage));
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private BufferedImage loadImage(String path) throws IOException {
        originalImage = ImageIO.read(new File(path));
        currentImage = originalImage;
        return originalImage;
    }
    private void salvarImagem2(BufferedImage image, String nome) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Imagem");
        fileChooser.setSelectedFile(new File(nome + ".png"));

        int escolha = fileChooser.showSaveDialog(null);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            File arquivoSalvo = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "png", arquivoSalvo);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Imagem salva com sucesso em: " + arquivoSalvo.getAbsolutePath(), "Salvar Imagem", JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar a imagem.", "Erro", JOptionPane.ERROR_MESSAGE);
                });
            }
        } else if (escolha == JFileChooser.CANCEL_OPTION) {
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Operação de salvar imagem cancelada pelo usuário.", "Cancelar", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
    private BufferedImage clarearImagem(BufferedImage image, int valor) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color originalColor = new Color(image.getRGB(x, y));
                int r = Math.min(255, Math.max(0, originalColor.getRed() + valor));
                int g = Math.min(255, Math.max(0, originalColor.getGreen() + valor));
                int b = Math.min(255, Math.max(0, originalColor.getBlue() + valor));

                result.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return result;
    }

    private BufferedImage escurecerImagem(BufferedImage image, int valor) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color originalColor = new Color(image.getRGB(x, y));
                int r = Math.max(0, originalColor.getRed() - valor);
                int g = Math.max(0, originalColor.getGreen() - valor);
                int b = Math.max(0, originalColor.getBlue() - valor);

                result.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return result;
    }

    private BufferedImage aplicarFiltroLaplaciano(BufferedImage image) {
        float[] matrix = {
            0, -1, 0,
            -1, 4, -1,
            0, -1, 0
        };
        BufferedImageOp laplacianFilter = new ConvolveOp(new Kernel(3, 3, matrix));
        return laplacianFilter.filter(image, null);
    }

    private BufferedImage aplicarFiltroMediana(BufferedImage image, int tamanhoFiltro) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = tamanhoFiltro / 2; x < width - tamanhoFiltro / 2; x++) {
            for (int y = tamanhoFiltro / 2; y < height - tamanhoFiltro / 2; y++) {
                int[] redValues = new int[tamanhoFiltro * tamanhoFiltro];
                int[] greenValues = new int[tamanhoFiltro * tamanhoFiltro];
                int[] blueValues = new int[tamanhoFiltro * tamanhoFiltro];

                for (int i = -tamanhoFiltro / 2; i <= tamanhoFiltro / 2; i++) {
                    for (int j = -tamanhoFiltro / 2; j <= tamanhoFiltro / 2; j++) {
                        Color color = new Color(image.getRGB(x + i, y + j));
                        redValues[(i + tamanhoFiltro / 2) * tamanhoFiltro + j + tamanhoFiltro / 2] = color.getRed();
                        greenValues[(i + tamanhoFiltro / 2) * tamanhoFiltro + j + tamanhoFiltro / 2] = color.getGreen();
                        blueValues[(i + tamanhoFiltro / 2) * tamanhoFiltro + j + tamanhoFiltro / 2] = color.getBlue();
                    }
                }

                int medianRed = findMedian(redValues);
                int medianGreen = findMedian(greenValues);
                int medianBlue = findMedian(blueValues);

                result.setRGB(x, y, new Color(medianRed, medianGreen, medianBlue).getRGB());
            }
        }

        return result;
    }

    private int findMedian(int[] values) {
        Arrays.sort(values);
        return values[values.length / 2];
    }

    private BufferedImage rotacionarImagem(BufferedImage image, int graus) {
        double radianos = Math.toRadians(graus);

        double sin = Math.abs(Math.sin(radianos));
        double cos = Math.abs(Math.cos(radianos));

        int largura = image.getWidth();
        int altura = image.getHeight();

        int novaLargura = (int) Math.floor(largura * cos + altura * sin);
        int novaAltura = (int) Math.floor(altura * cos + largura * sin);

        BufferedImage result = new BufferedImage(novaLargura, novaAltura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();

        AffineTransform transform = new AffineTransform();
        transform.translate((novaLargura - largura) / 2, (novaAltura - altura) / 2);
        transform.rotate(radianos, largura / 2.0, altura / 2.0);

        g.setTransform(transform);
        g.drawImage(image, 0, 0, null);

        g.dispose();

        return result;
    }

    private BufferedImage espelharImagem(BufferedImage image, String direcao) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int newX = x;
                int newY = y;

                if (direcao.equals("H")) {
                    newX = width - 1 - x;
                } else if (direcao.equals("V")) {
                    newY = height - 1 - y;
                }

                result.setRGB(newX, newY, image.getRGB(x, y));
            }
        }

        return result;
    }
    private boolean verificarPermissao() {
        if (planoPremium) {
            return true; 
        } else {
           
            return false;
        }
    }

    private void extrairRGB(BufferedImage image) {
    	if (!verificarPermissao()) {
	        JOptionPane.showMessageDialog(null, "Você não tem permissão para extrair canais RGB.", "Aviso", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
        boolean extrairCanalR = obterRespostaSimNao("Extrair canal R?");
        boolean extrairCanalG = obterRespostaSimNao("Extrair canal G?");
        boolean extrairCanalB = obterRespostaSimNao("Extrair canal B?");
        String enderecoSalvar = obterEnderecoSalvar();

        if (!extrairCanalR && !extrairCanalG && !extrairCanalB) {
            JOptionPane.showMessageDialog(null, "Nenhum canal selecionado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (extrairCanalR) {
            BufferedImage canalR = extrairCanal(image, 'R');
            salvarImagem(canalR, enderecoSalvar, "CanalR");
        }

        if (extrairCanalG) {
            BufferedImage canalG = extrairCanal(image, 'G');
            salvarImagem(canalG, enderecoSalvar, "CanalG");
        }

        if (extrairCanalB) {
            BufferedImage canalB = extrairCanal(image, 'B');
            salvarImagem(canalB, enderecoSalvar, "CanalB");
        }
    }

    private BufferedImage extrairCanal(BufferedImage image, char canal) {
    	 
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage canalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = new Color(image.getRGB(x, y));
                int valorCanal = 0;

                switch (canal) {
                    case 'R':
                        valorCanal = color.getRed();
                        break;
                    case 'G':
                        valorCanal = color.getGreen();
                        break;
                    case 'B':
                        valorCanal = color.getBlue();
                        break;
                }

                canalImage.setRGB(x, y, new Color(valorCanal, valorCanal, valorCanal).getRGB());
            }
        }

        return canalImage;
    }

    private boolean obterRespostaSimNao(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(null, mensagem, "Escolha", JOptionPane.YES_NO_OPTION);
        return resposta == JOptionPane.YES_OPTION;
    }

    private String obterEnderecoSalvar() {
        return JOptionPane.showInputDialog(null, "Digite o endereço para salvar os canais (ex: C:\\caminho\\para\\salvar\\)", "Salvar Canais", JOptionPane.PLAIN_MESSAGE);
    }

    private void salvarImagem(BufferedImage image, String endereco, String nome) {
        try {
            ImageIO.write(image, "png", new File(endereco + nome + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selecionarImagensParaUnirRGB() {
    	if (!verificarPermissao()) {
	        JOptionPane.showMessageDialog(null, "Você não tem permissão para unir canais RGB.", "Aviso", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
        canalR = selecionarImagem("Selecione a imagem para o canal R");
        canalG = selecionarImagem("Selecione a imagem para o canal G");
        canalB = selecionarImagem("Selecione a imagem para o canal B");
    }

    private BufferedImage selecionarImagem(String mensagem) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(mensagem);
        int escolha = fileChooser.showOpenDialog(null);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(fileChooser.getSelectedFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void atualizarLabel() {
        editedLabel.setIcon(new ImageIcon(currentImage));
    }

  
    private void unirRGB() {
    	  
        int width = canalR.getWidth();
        int height = canalR.getHeight();

        currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = new Color(canalR.getRGB(x, y)).getRed();
                int g = new Color(canalG.getRGB(x, y)).getGreen();
                int b = new Color(canalB.getRGB(x, y)).getBlue();
                currentImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

       
        atualizarLabel();
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }
}
