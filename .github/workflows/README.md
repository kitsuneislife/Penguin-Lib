# GitHub Actions Workflows

Este repositório contém os seguintes workflows automatizados:

## 🔧 build.yml - Build Principal
- **Trigger**: Push para `main`, Pull Requests, e releases
- **Função**: Compila o projeto, executa testes e gera artefatos
- **Artefatos**: JARs de build (retidos por 30 dias)

## 🚀 release.yml - Release Automática
- **Trigger**: Tags que seguem o padrão `v*.*.*` (ex: v1.0.0, v1.2.3)
- **Função**: Cria releases automáticas no GitHub com o JAR compilado
- **Como usar**:
  1. Crie uma tag: `git tag v1.0.0`
  2. Faça push da tag: `git push origin v1.0.0`
  3. O workflow criará automaticamente uma release com o JAR

## 🛠️ dev-build.yml - Builds de Desenvolvimento
- **Trigger**: Push para `main` ou `develop`, ou manual
- **Função**: Builds rápidos para desenvolvimento (snapshots)
- **Recursos**:
  - Pode ser executado manualmente via GitHub Actions UI
  - Pode ser pulado adicionando `[skip ci]` na mensagem do commit
  - Gera informações de build (data, commit, branch, autor)

## Como criar uma release:

1. **Certifique-se que está na branch main:**
   ```bash
   git checkout main
   git pull origin main
   ```

2. **Crie e faça push da tag:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **O workflow automaticamente:**
   - Compilará o projeto
   - Criará uma release no GitHub
   - Fará upload do JAR com nome formatado
   - Incluirá informações de instalação e requisitos

## Estrutura de Tags Recomendada:
- `v1.0.0` - Release principal
- `v1.0.1` - Patch/bugfix
- `v1.1.0` - Minor version com novas features
- `v2.0.0` - Major version com breaking changes

## Co-authoring:
Todos os releases incluem automaticamente o co-author thurzinhos nos release notes.
