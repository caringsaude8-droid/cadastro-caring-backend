package com.caring.cadastro.operadora.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class BeneficiarioRequestDTO {
    public Long benEmpId;
    public String benTipoMotivo;
    public String benCodUnimedSeg;
    public String benRelacaoDep;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Sao_Paulo")
    public Date benDtaNasc;

    public String benSexo;
    public String benEstCivil;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Sao_Paulo")
    public Date benDtaInclusao;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Sao_Paulo")
    public Date benDtaExclusao;

    public String benPlanoProd;
    public String benNomeSegurado;
    public String benCpf;
    public String benCidade;
    public String benUf;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Sao_Paulo")
    public Date benAdmissao;

    public String benNomeDaMae;
    public String benEndereco;
    public String benComplemento;
    public String benBairro;
    public String benCep;
    public String benMatricula;
    public String benDddCel;
    public String benEmail;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Sao_Paulo")
    public Date benDataCasamento;

    public String benIndicPesTrans;
    public String benNomeSocial;
    public String benIdentGenero;
    public Long benTitularId;
    public String benCodCartao;
    public String benMotivoExclusao;
    public String benStatus;
    public String benNumero;
}
