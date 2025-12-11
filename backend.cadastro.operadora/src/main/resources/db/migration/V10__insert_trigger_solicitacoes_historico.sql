-- V10__insert_trigger_solicitacoes_historico.sql
-- Cria trigger para registrar corretamente o valor das observações no histórico ao criar uma solicitação

CREATE OR REPLACE TRIGGER TRG_SOL_AUDITORIA_INSERT
    AFTER INSERT ON SGI_SOLICITACOES_BENEFICIARIO
    FOR EACH ROW
BEGIN
    -- Observação do solicitante
    IF :NEW.SOL_OBSERVACOES_SOLICITACAO IS NOT NULL THEN
        INSERT INTO SGI_SOLICITACOES_HISTORICO (
            HIS_SOL_ID,
            HIS_OPERACAO,
            HIS_CAMPO_ALTERADO,
            HIS_VALOR_ANTERIOR,
            HIS_VALOR_NOVO,
            HIS_USUARIO_ID,
            HIS_USUARIO_NOME,
            HIS_OBSERVACOES
        ) VALUES (
            :NEW.SOL_ID,
            'INSERT',
            'SOL_OBSERVACOES_SOLICITACAO',
            TO_CLOB(''),
            TO_CLOB(:NEW.SOL_OBSERVACOES_SOLICITACAO),
            :NEW.SOL_USUARIO_SOLICITANTE_ID,
            :NEW.SOL_USUARIO_SOLICITANTE_NOME,
            TO_CLOB('Observação do solicitante registrada')
        );
    ELSE
        INSERT INTO SGI_SOLICITACOES_HISTORICO (
            HIS_SOL_ID,
            HIS_OPERACAO,
            HIS_CAMPO_ALTERADO,
            HIS_VALOR_ANTERIOR,
            HIS_VALOR_NOVO,
            HIS_USUARIO_ID,
            HIS_USUARIO_NOME,
            HIS_OBSERVACOES
        ) VALUES (
            :NEW.SOL_ID,
            'INSERT',
            'SOL_OBSERVACOES_SOLICITACAO',
            TO_CLOB(''),
            TO_CLOB('Solicitação criada'),
            :NEW.SOL_USUARIO_SOLICITANTE_ID,
            :NEW.SOL_USUARIO_SOLICITANTE_NOME,
            TO_CLOB('Observação do solicitante registrada')
        );
    END IF;

    -- Observação geral
    IF :NEW.SOL_OBSERVACOES IS NOT NULL THEN
        INSERT INTO SGI_SOLICITACOES_HISTORICO (
            HIS_SOL_ID,
            HIS_OPERACAO,
            HIS_CAMPO_ALTERADO,
            HIS_VALOR_ANTERIOR,
            HIS_VALOR_NOVO,
            HIS_USUARIO_ID,
            HIS_USUARIO_NOME,
            HIS_OBSERVACOES
        ) VALUES (
            :NEW.SOL_ID,
            'INSERT',
            'SOL_OBSERVACOES',
            TO_CLOB(''),
            TO_CLOB(:NEW.SOL_OBSERVACOES),
            :NEW.SOL_USUARIO_SOLICITANTE_ID,
            :NEW.SOL_USUARIO_SOLICITANTE_NOME,
            TO_CLOB('Observação geral registrada')
        );
    END IF;

    -- Observação de aprovação
    IF :NEW.SOL_OBSERVACOES_APROVACAO IS NOT NULL THEN
        INSERT INTO SGI_SOLICITACOES_HISTORICO (
            HIS_SOL_ID,
            HIS_OPERACAO,
            HIS_CAMPO_ALTERADO,
            HIS_VALOR_ANTERIOR,
            HIS_VALOR_NOVO,
            HIS_USUARIO_ID,
            HIS_USUARIO_NOME,
            HIS_OBSERVACOES
        ) VALUES (
            :NEW.SOL_ID,
            'INSERT',
            'SOL_OBSERVACOES_APROVACAO',
            TO_CLOB(''),
            TO_CLOB(:NEW.SOL_OBSERVACOES_APROVACAO),
            :NEW.SOL_USUARIO_SOLICITANTE_ID,
            :NEW.SOL_USUARIO_SOLICITANTE_NOME,
            TO_CLOB('Observação de aprovação registrada')
        );
    END IF;
END;
/
