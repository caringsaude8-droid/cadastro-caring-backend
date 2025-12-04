-- V8__recreate_trigger_final.sql
-- Recria o trigger de auditoria corretamente com todos os campos CLOB

-- Força a exclusão do trig
-- ger se existir
BEGIN
    EXECUTE IMMEDIATE 'DROP TRIGGER TRG_SOL_AUDITORIA_UPDATE';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Recria o trigger completo e funcional
CREATE OR REPLACE TRIGGER TRG_SOL_AUDITORIA_UPDATE
    AFTER UPDATE ON SGI_SOLICITACOES_BENEFICIARIO
    FOR EACH ROW
DECLARE
    v_json VARCHAR2(4000);
BEGIN
    -- Monta o snapshot JSON como VARCHAR2 primeiro
    v_json := '{"status_anterior":"' || NVL(:OLD.SOL_STATUS, 'NULL') ||
              '","status_novo":"' || NVL(:NEW.SOL_STATUS, 'NULL') ||
              '","empresaId":"' || NVL(TO_CHAR(:NEW.SOL_EMP_ID), 'NULL') ||
              '","data_aprovacao":"' || NVL(TO_CHAR(:NEW.SOL_DATA_APROVACAO, 'YYYY-MM-DD HH24:MI:SS'), 'NULL') ||
              '","aprovador":"' || NVL(:NEW.SOL_APROVADOR_NOME, 'Sistema') || '"}';

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
                WHEN :NEW.SOL_STATUS = 'APROVADA' THEN 'Solicitacao aprovada: ' || NVL(SUBSTR(:NEW.SOL_OBSERVACOES_APROVACAO, 1, 200), 'Sem observacoes')
                WHEN :NEW.SOL_STATUS = 'REJEITADA' THEN 'Solicitacao rejeitada: ' || NVL(SUBSTR(:NEW.SOL_OBSERVACOES_APROVACAO, 1, 200), 'Sem motivo informado')
                ELSE 'Status alterado para ' || :NEW.SOL_STATUS
            END),
            TO_CLOB(v_json)
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

    -- Registrar outras alterações importantes
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
END;
/
