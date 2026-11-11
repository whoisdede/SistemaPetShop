package negocio;

import dados.Animal;
import dados.Cliente;
import dados.CompraFornecedor;
import dados.Estoque;
import dados.Fornecedor;
import dados.Funcionario;
import dados.ItemVenda;
import dados.Produto;
import dados.Servico;
import dados.Usuario;
import dados.Venda;
import excecoes.PersistenciaException;
import excecoes.RegraNegocioException;
import persistencia.AnimalDAO;
import persistencia.ClienteDAO;
import persistencia.CompraFornecedorDAO;
import persistencia.Conexao;
import persistencia.EstoqueDAO;
import persistencia.FornecedorDAO;
import persistencia.FuncionarioDAO;
import persistencia.InicializadorBanco;
import persistencia.ProdutoDAO;
import persistencia.RelatorioDAO;
import persistencia.ServicoDAO;
import persistencia.UsuarioDAO;
import persistencia.VendaDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaPetShop {

    private final InicializadorBanco inicializadorBanco;
    private final ClienteDAO clienteDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final UsuarioDAO usuarioDAO;
    private final AnimalDAO animalDAO;
    private final EstoqueDAO estoqueDAO;
    private final ProdutoDAO produtoDAO;
    private final FornecedorDAO fornecedorDAO;
    private final CompraFornecedorDAO compraFornecedorDAO;
    private final VendaDAO vendaDAO;
    private final ServicoDAO servicoDAO;
    private final RelatorioDAO relatorioDAO;

    public SistemaPetShop() {
        this.inicializadorBanco = new InicializadorBanco();
        this.clienteDAO = new ClienteDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.animalDAO = new AnimalDAO();
        this.estoqueDAO = new EstoqueDAO();
        this.produtoDAO = new ProdutoDAO();
        this.fornecedorDAO = new FornecedorDAO();
        this.compraFornecedorDAO = new CompraFornecedorDAO();
        this.vendaDAO = new VendaDAO();
        this.servicoDAO = new ServicoDAO();
        this.relatorioDAO = new RelatorioDAO();
    }


    // CONEXÃO E INICIALIZAÇÃO DO BANCO
    public void conectar(String host, String porta, String banco, String usuario, String senha)
            throws PersistenciaException {
        Conexao.configurar(host, porta, banco, usuario, senha);
        Conexao.getConexao();
    }

    public boolean isErroBancoInexistente(PersistenciaException ex) {
        return Conexao.isBancoInexistente(ex);
    }

    public void criarBancoEConectar() throws PersistenciaException {
        Conexao.criarBancoDeDados();
        Conexao.getConexao();
    }

    public boolean verificarEsquemaInstalado() throws PersistenciaException {
        return inicializadorBanco.verificarEsquemaInstalado();
    }

    public void inicializarBancoDeDados(boolean popularDadosTeste) throws PersistenciaException {
        inicializadorBanco.inicializarEsquema(popularDadosTeste);
    }

    public String getResumoConexao() {
        return Conexao.getResumoConexao();
    }

    public void encerrar() {
        Conexao.fecharConexao();
    }

    // CLIENTES
 
    public void cadastrarCliente(Cliente c) throws RegraNegocioException, PersistenciaException {
        validarCliente(c);
        clienteDAO.inserir(c);
    }

    public void atualizarCliente(Cliente c) throws RegraNegocioException, PersistenciaException {
        if (c.getIdCliente() <= 0) {
            throw new RegraNegocioException("Selecione um cliente válido na tabela para atualizar.");
        }
        validarCliente(c);
        clienteDAO.atualizar(c);
    }

    public void excluirCliente(int idCliente) throws RegraNegocioException, PersistenciaException {
        if (idCliente <= 0) {
            throw new RegraNegocioException("Selecione um cliente válido para excluir.");
        }
        clienteDAO.excluir(idCliente);
    }

    public List<Cliente> listarClientes() throws PersistenciaException {
        return clienteDAO.listarTodos();
    }

    public List<Cliente> filtrarClientes(String termo) throws PersistenciaException {
        if (termo == null || termo.isBlank()) {
            return clienteDAO.listarTodos();
        }
        return clienteDAO.buscarPorTermo(termo);
    }

    private void validarCliente(Cliente c) throws RegraNegocioException {
        if (c == null || c.getNome() == null || c.getNome().isBlank()) {
            throw new RegraNegocioException("O nome do cliente é obrigatório.");
        }
        if (c.getEmail() != null && !c.getEmail().isBlank() && !c.getEmail().contains("@")) {
            throw new RegraNegocioException("Informe um e-mail válido (ex.: cliente@email.com).");
        }
        if (c.getDataNascimento() != null && c.getDataNascimento().isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data de nascimento não pode ser uma data futura.");
        }
    }

    // ANIMAIS (PETS)

    public void cadastrarAnimal(Animal a) throws RegraNegocioException, PersistenciaException {
        validarAnimal(a);
        animalDAO.inserir(a);
    }

    public void atualizarAnimal(Animal a) throws RegraNegocioException, PersistenciaException {
        if (a.getIdAnimal() <= 0) {
            throw new RegraNegocioException("Selecione um animal válido na tabela para atualizar.");
        }
        validarAnimal(a);
        animalDAO.atualizar(a);
    }

    public void excluirAnimal(int idAnimal) throws RegraNegocioException, PersistenciaException {
        if (idAnimal <= 0) {
            throw new RegraNegocioException("Selecione um animal válido para excluir.");
        }
        animalDAO.excluir(idAnimal);
    }

    public List<Animal> listarAnimais() throws PersistenciaException {
        return animalDAO.listarTodos();
    }

    public List<Animal> filtrarAnimais(String termo) throws PersistenciaException {
        if (termo == null || termo.isBlank()) {
            return animalDAO.listarTodos();
        }
        return animalDAO.buscarPorFiltro(termo);
    }

    private void validarAnimal(Animal a) throws RegraNegocioException {
        if (a == null || a.getNome() == null || a.getNome().isBlank()) {
            throw new RegraNegocioException("O nome do animal é obrigatório.");
        }
        if (a.getEspecie() == null || a.getEspecie().isBlank()) {
            throw new RegraNegocioException("A espécie do animal é obrigatória.");
        }
        if (a.getCliente() == null) {
            throw new RegraNegocioException("Selecione o cliente proprietário do animal.");
        }
        if (a.getFuncionario() == null) {
            throw new RegraNegocioException("Selecione o funcionário responsável pelo atendimento.");
        }
        if (a.getDataNasc() != null && a.getDataAdmissao() != null
                && a.getDataNasc().isAfter(a.getDataAdmissao())) {
            throw new RegraNegocioException("A data de nascimento do animal não pode ser posterior à data de admissão.");
        }
    }

    // ESTOQUE E PRODUTOS
    
    public void cadastrarEstoque(Estoque e) throws RegraNegocioException, PersistenciaException {
        if (e == null || e.getQuantidadeMinima() < 0 || e.getQuantidadeAtual() < 0) {
            throw new RegraNegocioException("As quantidades mínima e atual do estoque não podem ser negativas.");
        }
        estoqueDAO.inserir(e);
    }

    public void atualizarEstoque(Estoque e) throws RegraNegocioException, PersistenciaException {
        if (e == null || e.getIdEstoque() <= 0) {
            throw new RegraNegocioException("Selecione um estoque válido para atualizar.");
        }
        if (e.getQuantidadeMinima() < 0 || e.getQuantidadeAtual() < 0) {
            throw new RegraNegocioException("As quantidades do estoque não podem ser negativas.");
        }
        estoqueDAO.atualizar(e);
    }

    public List<Estoque> listarEstoques() throws PersistenciaException {
        return estoqueDAO.listarTodos();
    }

    public void vincularAdministradorEstoque(Usuario u, Estoque e)
            throws RegraNegocioException, PersistenciaException {
        if (u == null || e == null) {
            throw new RegraNegocioException("Selecione um usuário e um estoque para vincular.");
        }
        estoqueDAO.vincularAdministrador(u.getIdUsuario(), e.getIdEstoque());
    }

    public List<String[]> listarAdministradoresEstoque() throws PersistenciaException {
        return estoqueDAO.listarAdministradores();
    }

    public void cadastrarProduto(Produto p) throws RegraNegocioException, PersistenciaException {
        validarProduto(p);
        produtoDAO.inserir(p);
    }

    public void atualizarProduto(Produto p) throws RegraNegocioException, PersistenciaException {
        if (p == null || p.getIdProduto() <= 0) {
            throw new RegraNegocioException("Selecione um produto válido na tabela para atualizar.");
        }
        validarProduto(p);
        produtoDAO.atualizar(p);
    }

    public void excluirProduto(int idProduto) throws RegraNegocioException, PersistenciaException {
        if (idProduto <= 0) {
            throw new RegraNegocioException("Selecione um produto válido para excluir.");
        }
        produtoDAO.excluir(idProduto);
    }

    public List<Produto> listarProdutos() throws PersistenciaException {
        return produtoDAO.listarTodos();
    }

    private void validarProduto(Produto p) throws RegraNegocioException {
        if (p == null || p.getNomeProduto() == null || p.getNomeProduto().isBlank()) {
            throw new RegraNegocioException("O nome do produto é obrigatório.");
        }
        if (p.getPreco() == null || p.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço de venda do produto deve ser maior que zero.");
        }
        if (p.getCusto() == null || p.getCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O preço de custo não pode ser negativo.");
        }
        if (p.getEstoque() == null) {
            throw new RegraNegocioException("Selecione o estoque ao qual o produto está vinculado.");
        }
    }

    // FORNECEDORES E COMPRAS DE REPOSIÇÃO

    public void cadastrarFornecedor(Fornecedor f) throws RegraNegocioException, PersistenciaException {
        if (f == null || f.getNome() == null || f.getNome().isBlank()) {
            throw new RegraNegocioException("O nome fantasia do fornecedor é obrigatório.");
        }
        fornecedorDAO.inserir(f);
    }

    public void excluirFornecedor(int idFornecedor) throws RegraNegocioException, PersistenciaException {
        if (idFornecedor <= 0) {
            throw new RegraNegocioException("Selecione um fornecedor válido para excluir.");
        }
        fornecedorDAO.excluir(idFornecedor);
    }

    public List<Fornecedor> listarFornecedores() throws PersistenciaException {
        return fornecedorDAO.listarTodos();
    }

    public void registrarCompraFornecedor(CompraFornecedor cf)
            throws RegraNegocioException, PersistenciaException {
        if (cf == null || cf.getFornecedor() == null) {
            throw new RegraNegocioException("Selecione o fornecedor da compra.");
        }
        if (cf.getProduto() == null) {
            throw new RegraNegocioException("Selecione o produto adquirido.");
        }
        if (cf.getData() == null) {
            cf.setData(LocalDate.now());
        }
        if (cf.getQtd() <= 0) {
            throw new RegraNegocioException("A quantidade comprada deve ser maior que zero.");
        }
        compraFornecedorDAO.registrarCompra(cf);
    }

    public List<CompraFornecedor> listarComprasFornecedor() throws PersistenciaException {
        return compraFornecedorDAO.listarTodas();
    }

    // VENDAS (PDV + ITENS DE VENDA)

    public void registrarVenda(Venda venda) throws RegraNegocioException, PersistenciaException {
        if (venda == null || venda.getCliente() == null) {
            throw new RegraNegocioException("Selecione o cliente da venda.");
        }
        if (venda.getDataVenda() == null) {
            venda.setDataVenda(LocalDate.now());
        }
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new RegraNegocioException("Adicione ao menos um item de produto para concluir a venda.");
        }

        // Consolida itens repetidos do mesmo produto para respeitar a PK (idVenda, idProduto)
        Map<Integer, ItemVenda> consolidados = new LinkedHashMap<>();
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() == null) {
                throw new RegraNegocioException("Item de venda sem produto informado.");
            }
            if (item.getQtd() <= 0) {
                throw new RegraNegocioException("A quantidade de cada item deve ser maior que zero.");
            }
            if (item.getValorUnit() == null || item.getValorUnit().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("O valor unitário do item deve ser maior que zero.");
            }

            int idProd = item.getProduto().getIdProduto();
            ItemVenda existente = consolidados.get(idProd);
            if (existente == null) {
                consolidados.put(idProd, new ItemVenda(item.getProduto(), item.getValorUnit(), item.getQtd()));
            } else {
                existente.setQtd(existente.getQtd() + item.getQtd());
            }
        }
        venda.setItens(new ArrayList<>(consolidados.values()));

        vendaDAO.registrarVenda(venda);
    }

    public List<Venda> listarVendas() throws PersistenciaException {
        return vendaDAO.listarTodasComItens();
    }

    // SERVIÇOS, FUNCIONÁRIOS E USUÁRIOS

    public void cadastrarFuncionario(Funcionario f) throws RegraNegocioException, PersistenciaException {
        if (f == null || f.getNome() == null || f.getNome().isBlank()) {
            throw new RegraNegocioException("O nome do funcionário é obrigatório.");
        }
        if (f.getSalario() == null || f.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O salário do funcionário deve ser maior que zero.");
        }
        funcionarioDAO.inserir(f);
    }

    public List<Funcionario> listarFuncionarios() throws PersistenciaException {
        return funcionarioDAO.listarTodos();
    }

    public void cadastrarUsuario(Usuario u) throws RegraNegocioException, PersistenciaException {
        if (u == null || u.getLogin() == null || u.getLogin().isBlank()) {
            throw new RegraNegocioException("O login do usuário é obrigatório.");
        }
        if (u.getSenha() == null || u.getSenha().length() < 4) {
            throw new RegraNegocioException("A senha deve possuir ao menos 4 caracteres.");
        }
        if (u.getFuncionario() == null) {
            throw new RegraNegocioException("Selecione o funcionário vinculado ao usuário.");
        }
        usuarioDAO.inserir(u);
    }

    public List<Usuario> listarUsuarios() throws PersistenciaException {
        return usuarioDAO.listarTodos();
    }

    public void registrarServico(Servico s) throws RegraNegocioException, PersistenciaException {
        if (s == null || s.getTipoServico() == null || s.getTipoServico().isBlank()) {
            throw new RegraNegocioException("Informe o tipo de serviço realizado.");
        }
        if (s.getValor() == null || s.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor do serviço deve ser maior que zero.");
        }
        if (s.getCliente() == null) {
            throw new RegraNegocioException("Selecione o cliente atendido no serviço.");
        }
        if (s.getUsuario() == null) {
            throw new RegraNegocioException("Selecione o usuário/operador que registrou o serviço.");
        }
        if (s.getDataTransacao() == null) {
            s.setDataTransacao(LocalDate.now());
        }
        servicoDAO.inserir(s);
    }

    public List<Servico> listarServicos() throws PersistenciaException {
        return servicoDAO.listarTodos();
    }

    // RELATÓRIOS E CONSULTAS GERENCIAIS (FUNCTIONS PL/pgSQL)

    public List<String[]> relatorioEstoqueBaixo() throws PersistenciaException {
        return relatorioDAO.consultarEstoqueBaixo();
    }

    public List<String[]> relatorioRankingProdutos(int limite)
            throws RegraNegocioException, PersistenciaException {
        if (limite <= 0) {
            throw new RegraNegocioException("O limite do ranking deve ser maior que zero.");
        }
        return relatorioDAO.consultarRankingProdutos(limite);
    }

    public List<String[]> relatorioFaturamentoPeriodo(LocalDate inicio, LocalDate fim)
            throws RegraNegocioException, PersistenciaException {
        if (inicio == null || fim == null) {
            throw new RegraNegocioException("Informe as datas inicial e final do período.");
        }
        if (inicio.isAfter(fim)) {
            throw new RegraNegocioException("A data inicial não pode ser maior que a data final.");
        }
        return relatorioDAO.consultarFaturamentoPeriodo(inicio, fim);
    }

    public List<String[]> relatorioResumoGastoCliente(Cliente c)
            throws RegraNegocioException, PersistenciaException {
        if (c == null) {
            throw new RegraNegocioException("Selecione um cliente para consultar.");
        }
        return relatorioDAO.consultarResumoGastoCliente(c.getIdCliente());
    }

    public List<String[]> relatorioHistoricoComprasCliente(Cliente c)
            throws RegraNegocioException, PersistenciaException {
        if (c == null) {
            throw new RegraNegocioException("Selecione um cliente para consultar o histórico.");
        }
        return relatorioDAO.consultarHistoricoComprasCliente(c.getIdCliente());
    }
}
