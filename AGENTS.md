# AGENTS.md

Two independent projects; run every command from its own directory (no root build).

- `dn-ai-code-mother-backend/` — Spring Boot 3.5.3, Java 21, Maven wrapper
- `dn-ai-code-mother-frontend/` — Vue 3 + Vite 8 + TypeScript + Pinia + Ant Design Vue, npm

## Backend

- Run: `cd dn-ai-code-mother-backend && ./mvnw spring-boot:run` (`mvnw.cmd` on native Windows shells)
- Test: `./mvnw test`; single test: `./mvnw test -Dtest=ClassName#method`; compile only: `./mvnw -DskipTests compile`
- Serves on `http://localhost:8123/api` — `/api` is the servlet context path, so controller mappings must not include it.
- Health check is `GET /api/health/` (trailing slash). `GET /api/health` returns 404 because Spring 6 trailing-slash matching is off.
- API docs: Knife4j UI at `/api/doc.html`, JSON at `/api/v3/api-docs`. springdoc only scans `com.dn.dnaicodemotherbackend.controller`.
- Responses are always `BaseResponse<T>` (`{code, data, message}`) built with `ResultUtils.success/error`; success code is `0`. Errors also return HTTP 200 — clients must inspect `code`, not the HTTP status.
- Throw `BusinessException`/use `ThrowUtils.throwIf` with an `ErrorCode`; `GlobalExceptionHandler` converts these, and any other `RuntimeException` into a generic `SYSTEM_ERROR`.
- MySQL driver is on the classpath, but there is no JDBC starter, datasource config, ORM, or migrations yet. Do not assume a database exists.
- No Jakarta Bean Validation provider is installed; `@Valid`/`@NotBlank` are silently ineffective unless you add `spring-boot-starter-validation`.
- Lombok is the only annotation processor; it is configured explicitly in `pom.xml`. Keep Java at 21.

## Frontend

- Setup: `cd dn-ai-code-mother-frontend && npm install`, then `npm run dev`
- `package.json` requires Node `^22.18.0 || >=24.12.0`; older Node runs but is unsupported.
- Checks: `npm run type-check` (`vue-tsc --build`, no emit) and `npm run build` (type-check + Vite build).
- `npm run lint` runs oxlint then ESLint, both with `--fix` — linting rewrites files. `npm run format` runs Prettier on `src/` only.
- Style: no semicolons, single quotes, 100-col lines, LF endings (`.prettierrc.json`, `.editorconfig`).
- `@/` maps to `src/` in both `vite.config.ts` and `tsconfig.app.json`; keep them in sync.
- Ant Design Vue is registered globally in `src/main.ts`, so components need no per-file import.
- No Vite proxy is configured: `/api` calls from the dev server will not reach the backend. Use an absolute URL or add `server.proxy` first.
- No frontend test framework is installed; do not add or invoke `npm test` without setting one up.

## Verifying a change

- Backend: `./mvnw test` (currently only a context-load test).
- Frontend: `npm run type-check`; run `npm run lint` only if file rewrites are acceptable.
- For cross-stack changes run both, since the two projects share no build.
