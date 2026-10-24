package com.dn.dnaicodemotherbackend.core.saver;

import cn.hutool.core.util.StrUtil;
import com.dn.dnaicodemotherbackend.ai.model.MultiFileCodeResult;
import com.dn.dnaicodemotherbackend.exception.BusinessException;
import com.dn.dnaicodemotherbackend.exception.ErrorCode;
import com.dn.dnaicodemotherbackend.model.enums.CodeGenTypeEnum;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 22:26
 **/
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        if (StrUtil.isEmpty(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 内容不能为空");
        }
    }

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFile(MultiFileCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());

        if (StrUtil.isNotBlank(result.getCssCode())) {
            writeToFile(baseDirPath, "style.css", result.getCssCode());
        }

        if (StrUtil.isNotBlank(result.getJsCode())) {
            writeToFile(baseDirPath, "script.js", result.getJsCode());
        }
    }
}
