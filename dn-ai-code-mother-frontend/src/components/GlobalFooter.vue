<template>
  <a-layout-footer class="global-footer">
    <p class="global-footer__copyright">
      <a
        class="global-footer__link"
        href="https://github.com/linjingdong"
        target="_blank"
        rel="noopener noreferrer"
      >
        豆泥搭搭 by donny
      </a>
    </p>
  </a-layout-footer>
</template>

<style scoped>
.global-footer {
  flex-shrink: 0;
  padding: 16px 24px;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.45);
  text-align: center;
  background: #fff;
  border-top: 1px solid #f0f0f0;
}

.global-footer__copyright {
  margin: 0;
}

.global-footer__link {
  color: inherit;
  text-decoration: none;
  transition: color 0.2s;
}

.global-footer__link:hover {
  color: #1677ff;
}
</style>
