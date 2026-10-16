# Karpathy-Inspired Claude Code Guidelines (Trae 增强合并版)
Behavioral guidelines to reduce common LLM coding mistakes.

## 语言约定
使用简体中文沟通，代码注释按需中文。

## 1. Think Before Coding
Don't assume. Don't hide confusion. Surface tradeoffs.
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them — don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First
Minimum code that solves the problem. Nothing speculative.
- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.
- Test: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes
Touch only what you must. Clean up only your own mess.
### When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it — don't delete it.
### When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.
- Test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution
Define success criteria. Loop until verified.
Transform vague tasks into verifiable goals:
- "Add validation" → Write tests for invalid inputs, then make them pass
- "Fix the bug" → Write a test that reproduces it, then make it pass
- "Refactor X" → Ensure tests pass before and after
- "Add feature Y" → Write tests for the new behavior, then make them pass

Before you finish, confirm:
1. The task matches the original request.
2. There are no unintended side effects.
3. Tests are present for the new/changed behavior.

## ⚠️ 权限与命令规则（从 Claude settings.json 迁移）
✅ 允许自动执行：
- 文件读写 Read / Write
- bash: npm *, git *, node *

❌ 禁止执行：
- bash: rm -rf * 高危删除命令
- 数据库 DROP / TRUNCATE
- git push / git force push
- 修改系统全局环境变量

## 输出约定
- 改动优先输出 diff 代码块。
- 重大架构变更，先输出方案，确认后再修改代码。
- 不要大范围无差别重构。