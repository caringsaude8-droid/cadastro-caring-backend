-- V7__fix_trigger_solicitacoes_beneficiario.sql
-- Recria o trigger de auditoria para garantir que está válido após adição da coluna SOL_EMP_ID

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
            :OLD.SOL_STATUS,
            :NEW.SOL_STATUS,
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            CASE
                WHEN :NEW.SOL_STATUS = 'APROVADA' THEN 'Solicitação aprovada: ' || NVL(:NEW.SOL_OBSERVACOES_APROVACAO, 'Sem observações')
                WHEN :NEW.SOL_STATUS = 'REJEITADA' THEN 'Solicitação rejeitada: ' || NVL(:NEW.SOL_OBSERVACOES_APROVACAO, 'Sem motivo informado')
                ELSE 'Status alterado para ' || :NEW.SOL_STATUS
            END,
            TO_CLOB('{"status_anterior":"' || :OLD.SOL_STATUS || '","status_novo":"' || :NEW.SOL_STATUS || '","empresaId":"' || NVL(TO_CHAR(:NEW.SOL_EMP_ID),'NULL') || '","data_aprovacao":"' || TO_CHAR(:NEW.SOL_DATA_APROVACAO, 'YYYY-MM-DD HH24:MI:SS') || '","aprovador":"' || NVL(:NEW.SOL_APROVADOR_NOME, 'Sistema') || '"}')
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
            NVL(TO_CHAR(:OLD.SOL_EMP_ID),'NULL'),
            NVL(TO_CHAR(:NEW.SOL_EMP_ID),'NULL'),
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            'Empresa vinculada alterada'
        );
    END IF;

    -- Registrar outras alterações importantes
    IF NVL(:OLD.SOL_OBSERVACOES_APROVACAO, 'NULL') != NVL(:NEW.SOL_OBSERVACOES_APROVACAO, 'NULL') THEN
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
            :OLD.SOL_OBSERVACOES_APROVACAO,
            :NEW.SOL_OBSERVACOES_APROVACAO,
            :NEW.SOL_APROVADOR_ID,
            :NEW.SOL_APROVADOR_NOME,
            'Observações de aprovação adicionadas/alteradas'
        );
    END IF;
END;
/
