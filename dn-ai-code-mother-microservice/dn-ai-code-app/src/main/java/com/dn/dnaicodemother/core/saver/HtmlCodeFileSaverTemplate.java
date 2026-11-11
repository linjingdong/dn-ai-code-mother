package com.dn.dnaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.dn.dnaicodemother.ai.model.HtmlCodeResult;
import com.dn.dnaicodemother.exception.BusinessException;
import com.dn.dnaicodemother.exception.ErrorCode;
import com.dn.dnaicodemother.model.enums.CodeGenTypeEnum;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 22:21
 **/
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {
    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if (StrUtil.isEmpty(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码内容不能为空");
        }
    }

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFile(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }
}
