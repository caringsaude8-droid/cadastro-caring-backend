-- V9__fix_trigger_definitive.sql
-- Corrige definitivamente o trigger mantendo o campo HIS_SNAPSHOT_COMPLETO com JSON_OBJECT

-- Remove o trigger
BEGIN
    EXECUTE IMMEDIATE 'DROP TRIGGER TRG_SOL_AUDITORIA_UPDATE';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Cria trigger completo COM o campo HIS_SNAPSHOT_COMPLETO usando JSON_OBJECT
CREATE OR REPLACE TRIGGER TRG_SOL_AUDITORIA_UPDATE
    AFTER UPDATE ON SGI_SOLICITACOES_BENEFICIARIO
    FOR EACH ROW
BEGIN
    -- Registrar mudança de status
    IF :OLD.SOL_STATUS != :NEW.SOL_STATUS THEN
        INSERT INTO SGI_SOLICITACOES_HISTORICO (
            HIS_SOL_ID,
            HIS_OPERACAO,
            HIS_STATUS_ANTERIOR,
            HIS_STATUS_NOVO,
            HIS_CAMPO_ALTERADO,
            HIS_VALOR_ANTERIOR,
            HIS_VALOR_NOVO,
            HIS_USUARIO_ID,
            HIS_USUARIO_NOME,
            HIS_OBSERVACOES,
            HIS_SNAPSHOT_COMPLETO
        ) VALUES (
            :NEW.SOL_ID,
            'UPDATE',
            :OLD.SOL_STATUS,
            :NEW.SOL_STATUS,
            'SOL_STATUS',
            TO_CLOB(:OLD.SOL_STATUS),
            TO_CLOB(:NEW.SOL_STATUS),
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            TO_CLOB(CASE
                WHEN :NEW.SOL_STATUS = 'APROVADA' THEN 'Solicitacao aprovada'
                WHEN :NEW.SOL_STATUS = 'REJEITADA' THEN 'Solicitacao rejeitada'
                ELSE 'Status alterado'
            END),
            TO_CLOB('{"status_anterior":"' || NVL(:OLD.SOL_STATUS, 'NULL') || '","status_novo":"' || NVL(:NEW.SOL_STATUS, 'NULL') || '","empresaId":"' || NVL(TO_CHAR(:NEW.SOL_EMP_ID), 'NULL') || '","aprovador":"' || NVL(:NEW.SOL_APROVADOR_NOME, 'Sistema') || '"}')
        );
    END IF;

    -- Registrar alterações do campo SOL_EMP_ID
    IF NVL(:OLD.SOL_EMP_ID, -1) != NVL(:NEW.SOL_EMP_ID, -1) THEN
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
            'UPDATE',
            'SOL_EMP_ID',
            TO_CLOB(NVL(TO_CHAR(:OLD.SOL_EMP_ID),'NULL')),
            TO_CLOB(NVL(TO_CHAR(:NEW.SOL_EMP_ID),'NULL')),
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            TO_CLOB('Empresa vinculada alterada')
        );
    END IF;

    -- Registrar alterações do campo SOL_OBSERVACOES_APROVACAO
    IF NVL(SUBSTR(:OLD.SOL_OBSERVACOES_APROVACAO, 1, 200), 'NULL') != NVL(SUBSTR(:NEW.SOL_OBSERVACOES_APROVACAO, 1, 200), 'NULL') THEN
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
            'UPDATE',
            'SOL_OBSERVACOES_APROVACAO',
            TO_CLOB(SUBSTR(:OLD.SOL_OBSERVACOES_APROVACAO, 1, 200)),
            TO_CLOB(SUBSTR(:NEW.SOL_OBSERVACOES_APROVACAO, 1, 200)),
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            TO_CLOB('Observacoes de aprovacao adicionadas/alteradas')
        );
    END IF;

    -- Registrar alterações do campo SOL_OBSERVACOES_SOLICITACAO
    IF NVL(SUBSTR(:OLD.SOL_OBSERVACOES_SOLICITACAO, 1, 200), 'NULL') != NVL(SUBSTR(:NEW.SOL_OBSERVACOES_SOLICITACAO, 1, 200), 'NULL') THEN
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
            'UPDATE',
            'SOL_OBSERVACOES_SOLICITACAO',
            TO_CLOB(SUBSTR(:OLD.SOL_OBSERVACOES_SOLICITACAO, 1, 200)),
            TO_CLOB(SUBSTR(:NEW.SOL_OBSERVACOES_SOLICITACAO, 1, 200)),
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            TO_CLOB('Observacoes do solicitante adicionadas/alteradas')
        );
    END IF;

    -- Registrar alterações do campo SOL_OBSERVACOES (geral)
    IF NVL(SUBSTR(:OLD.SOL_OBSERVACOES, 1, 200), 'NULL') != NVL(SUBSTR(:NEW.SOL_OBSERVACOES, 1, 200), 'NULL') THEN
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
            'UPDATE',
            'SOL_OBSERVACOES',
            TO_CLOB(SUBSTR(:OLD.SOL_OBSERVACOES, 1, 200)),
            TO_CLOB(SUBSTR(:NEW.SOL_OBSERVACOES, 1, 200)),
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            TO_CLOB('Observacoes gerais adicionadas/alteradas')
        );
    END IF;
END;
/
