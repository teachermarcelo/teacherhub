-- TeacherDash Schema para Supabase
-- Tabelas NOVAS com prefixo teacher_ e UUIDs
-- Copie e execute tudo isso no SQL Editor do Supabase

-- ============================================
-- 1. TABELA DE USUÁRIOS (Professores)
-- ============================================
CREATE TABLE teacher_users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  auth_id UUID UNIQUE, -- Relação com Supabase Auth
  email VARCHAR(255) UNIQUE NOT NULL,
  nome VARCHAR(255) NOT NULL,
  disciplina VARCHAR(100),
  num_turmas INT DEFAULT 0,
  plano VARCHAR(50) DEFAULT 'free', -- free, pro, enterprise
  pix_ativo BOOLEAN DEFAULT false,
  pix_key VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_users_auth_id ON teacher_users(auth_id);
CREATE INDEX idx_teacher_users_email ON teacher_users(email);

-- ============================================
-- 2. TABELA DE ALUNOS DO TEACHERDASH
-- ============================================
CREATE TABLE teacher_alunos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  teacher_user_id UUID NOT NULL REFERENCES teacher_users(id) ON DELETE CASCADE,
  nome VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  telefone VARCHAR(20),
  foto_url VARCHAR(500),
  status VARCHAR(50) DEFAULT 'ativo', -- ativo, inativo, evadido
  data_inscricao TIMESTAMP DEFAULT now(),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_alunos_teacher_user_id ON teacher_alunos(teacher_user_id);
CREATE INDEX idx_teacher_alunos_email ON teacher_alunos(email);

-- ============================================
-- 3. TABELA DE TURMAS DO TEACHERDASH
-- ============================================
CREATE TABLE teacher_turmas (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  teacher_user_id UUID NOT NULL REFERENCES teacher_users(id) ON DELETE CASCADE,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  horario VARCHAR(100), -- Ex: "19h de seg a sex"
  proxima_aula TIMESTAMP,
  media_turma DECIMAL(3,1) DEFAULT 0,
  total_alunos INT DEFAULT 0,
  status VARCHAR(50) DEFAULT 'ativa', -- ativa, pausada, finalizada
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_turmas_teacher_user_id ON teacher_turmas(teacher_user_id);

-- ============================================
-- 4. TABELA DE INSCRIÇÕES (Aluno + Turma)
-- ============================================
CREATE TABLE teacher_inscricoes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  aluno_id UUID NOT NULL REFERENCES teacher_alunos(id) ON DELETE CASCADE,
  turma_id UUID NOT NULL REFERENCES teacher_turmas(id) ON DELETE CASCADE,
  nota_atual DECIMAL(3,1) DEFAULT 0,
  frequencia INT DEFAULT 0, -- percentual
  data_inscricao TIMESTAMP DEFAULT now(),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE(aluno_id, turma_id)
);

CREATE INDEX idx_teacher_inscricoes_aluno_id ON teacher_inscricoes(aluno_id);
CREATE INDEX idx_teacher_inscricoes_turma_id ON teacher_inscricoes(turma_id);

-- ============================================
-- 5. TABELA DE ATIVIDADES
-- ============================================
CREATE TABLE teacher_atividades (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  turma_id UUID NOT NULL REFERENCES teacher_turmas(id) ON DELETE CASCADE,
  titulo VARCHAR(255) NOT NULL,
  descricao TEXT,
  tipo VARCHAR(50), -- exercicio, prova, trabalho, quiz
  data_entrega TIMESTAMP NOT NULL,
  valor_maximo DECIMAL(5,2) DEFAULT 10,
  status VARCHAR(50) DEFAULT 'ativa', -- ativa, corrigindo, finalizada
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_atividades_turma_id ON teacher_atividades(turma_id);

-- ============================================
-- 6. TABELA DE SUBMISSÕES (Aluno responde atividade)
-- ============================================
CREATE TABLE teacher_submissoes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  atividade_id UUID NOT NULL REFERENCES teacher_atividades(id) ON DELETE CASCADE,
  aluno_id UUID NOT NULL REFERENCES teacher_alunos(id) ON DELETE CASCADE,
  arquivo_url VARCHAR(500),
  comentario TEXT,
  nota DECIMAL(5,2),
  feedback TEXT,
  data_entrega TIMESTAMP DEFAULT now(),
  data_correcao TIMESTAMP,
  status VARCHAR(50) DEFAULT 'pendente', -- pendente, corrigida, rejeitada
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_submissoes_atividade_id ON teacher_submissoes(atividade_id);
CREATE INDEX idx_teacher_submissoes_aluno_id ON teacher_submissoes(aluno_id);

-- ============================================
-- 7. TABELA DE FATURAS
-- ============================================
CREATE TABLE teacher_faturas (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  teacher_user_id UUID NOT NULL REFERENCES teacher_users(id) ON DELETE CASCADE,
  aluno_id UUID NOT NULL REFERENCES teacher_alunos(id) ON DELETE CASCADE,
  turma_id UUID NOT NULL REFERENCES teacher_turmas(id) ON DELETE CASCADE,
  valor DECIMAL(10,2) NOT NULL,
  descricao VARCHAR(255), -- Ex: "Mensalidade maio/2026"
  data_vencimento DATE NOT NULL,
  data_pagamento TIMESTAMP,
  status VARCHAR(50) DEFAULT 'pendente', -- pendente, pago, atrasado, cancelado
  metodo_pagamento VARCHAR(50), -- pix, boleto, cartao
  chave_pix VARCHAR(255),
  observacao TEXT,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_faturas_teacher_user_id ON teacher_faturas(teacher_user_id);
CREATE INDEX idx_teacher_faturas_aluno_id ON teacher_faturas(aluno_id);
CREATE INDEX idx_teacher_faturas_turma_id ON teacher_faturas(turma_id);
CREATE INDEX idx_teacher_faturas_status ON teacher_faturas(status);
CREATE INDEX idx_teacher_faturas_vencimento ON teacher_faturas(data_vencimento);

-- ============================================
-- 8. TABELA DE RELATÓRIOS
-- ============================================
CREATE TABLE teacher_relatorios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  teacher_user_id UUID NOT NULL REFERENCES teacher_users(id) ON DELETE CASCADE,
  tipo VARCHAR(50), -- desempenho, financeiro, frequencia
  titulo VARCHAR(255) NOT NULL,
  conteudo JSONB, -- armazena dados do relatório em JSON
  data_geracao TIMESTAMP DEFAULT now(),
  created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_relatorios_teacher_user_id ON teacher_relatorios(teacher_user_id);

-- ============================================
-- 9. TABELA DE NOTIFICAÇÕES
-- ============================================
CREATE TABLE teacher_notificacoes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  teacher_user_id UUID NOT NULL REFERENCES teacher_users(id) ON DELETE CASCADE,
  tipo VARCHAR(50), -- fatura_vencida, aluno_inscrito, nota_baixa
  titulo VARCHAR(255) NOT NULL,
  mensagem TEXT,
  lida BOOLEAN DEFAULT false,
  data_leitura TIMESTAMP,
  created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_teacher_notificacoes_teacher_user_id ON teacher_notificacoes(teacher_user_id);
CREATE INDEX idx_teacher_notificacoes_lida ON teacher_notificacoes(lida);

-- ============================================
-- 10. TABELA DE CONFIGURAÇÕES DO USUÁRIO
-- ============================================
CREATE TABLE teacher_configuracoes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  teacher_user_id UUID NOT NULL UNIQUE REFERENCES teacher_users(id) ON DELETE CASCADE,
  email_notificacoes BOOLEAN DEFAULT true,
  notificacoes_fatura BOOLEAN DEFAULT true,
  notificacoes_aluno BOOLEAN DEFAULT true,
  tema VARCHAR(20) DEFAULT 'dark', -- dark, light
  idioma VARCHAR(10) DEFAULT 'pt-BR',
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

-- ============================================
-- VIEWS ÚTEIS
-- ============================================

-- View: Resumo financeiro por professor
CREATE OR REPLACE VIEW teacher_vw_resumo_financeiro AS
SELECT 
  u.id as teacher_user_id,
  u.nome as professor,
  COUNT(DISTINCT f.id) as total_faturas,
  COUNT(DISTINCT CASE WHEN f.status = 'pago' THEN f.id END) as faturas_pagas,
  COUNT(DISTINCT CASE WHEN f.status = 'pendente' THEN f.id END) as faturas_pendentes,
  COALESCE(SUM(CASE WHEN f.status = 'pago' THEN f.valor END), 0) as receita_total,
  COALESCE(SUM(CASE WHEN f.status = 'pendente' THEN f.valor END), 0) as pendente_total
FROM teacher_users u
LEFT JOIN teacher_faturas f ON u.id = f.teacher_user_id
GROUP BY u.id, u.nome;

-- View: Desempenho de alunos por turma
CREATE OR REPLACE VIEW teacher_vw_desempenho_alunos AS
SELECT 
  t.id as turma_id,
  t.nome as turma,
  a.id as aluno_id,
  a.nome as aluno,
  i.nota_atual,
  i.frequencia
FROM teacher_turmas t
LEFT JOIN teacher_inscricoes i ON t.id = i.turma_id
LEFT JOIN teacher_alunos a ON i.aluno_id = a.id
ORDER BY t.nome, a.nome;

-- ============================================
-- POLÍTICAS RLS (Row Level Security)
-- ============================================

ALTER TABLE teacher_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_alunos ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_turmas ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_inscricoes ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_atividades ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_submissoes ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_faturas ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_notificacoes ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_configuracoes ENABLE ROW LEVEL SECURITY;

-- Policy: usuários só veem seus próprios dados
CREATE POLICY "teacher_users_own_data" ON teacher_users
FOR SELECT USING (auth.uid() = auth_id);

CREATE POLICY "teacher_alunos_owner" ON teacher_alunos
FOR SELECT USING (auth.uid() IN (SELECT auth_id FROM teacher_users WHERE id = teacher_user_id));

CREATE POLICY "teacher_turmas_owner" ON teacher_turmas
FOR SELECT USING (auth.uid() IN (SELECT auth_id FROM teacher_users WHERE id = teacher_user_id));

CREATE POLICY "teacher_faturas_owner" ON teacher_faturas
FOR SELECT USING (auth.uid() IN (SELECT auth_id FROM teacher_users WHERE id = teacher_user_id));

-- ============================================
-- FIM DO SCHEMA TEACHERDASH
-- ============================================
