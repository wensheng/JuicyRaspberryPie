<!-- TODO: Please describe your changes here. -->

---
<!-- Git and GitHub -->
- [ ] The main commit(s) reference the Fibery ticket via a `TASK-NNNN` prefix in the commit message subject
- [ ] Include a human-readable description of what the pull request is trying to accomplish
- [ ] The CI build passes
---
<!-- Testing; only one of the following needs to be checked: -->
- [ ] New automated tests have been added
- [ ] The changes are already covered by automated tests and have been adapted
- [ ] These manual test cases cover this change:
- [ ] Steps for the reviewer(s) on how they can manually QA the changes:
- [ ] This is a minor internal change; basic CI/CD coverage is enough
- [ ] This is an incomplete feature hidden behind feature flag:
- [ ] This is proof-of-concept / experimental code not for production / marked `@Deprecated`
- [ ] No (significant) changes to production code
<!-- Development -->
- [ ] The change also works in LogicTestServer (and does not break it)
- [ ] The change also works in the local Docker Compose environment
- [ ] The change also works under Bedrock edition
---
<!-- Documentation -->
- [ ] Classes and public methods have documentation (that doesn't just repeat the technical subject in English)
- [ ] Logging is implemented to monitor feature usage and troubleshoot problems in production
- [ ] These ReWiki pages are affected by this change and will be adapted:
