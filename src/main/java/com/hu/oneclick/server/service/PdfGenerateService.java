package com.hu.oneclick.server.service;

import com.hu.oneclick.model.param.SignOffParam;

public interface PdfGenerateService {
    void generatePdf(SignOffParam signOffParam);
}
