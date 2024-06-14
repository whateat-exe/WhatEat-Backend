package com.exe.whateat.application.common;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@RequestMapping("${whateat.api.path}")
public abstract class AbstractController {

    // Intentionally ignored.
}
