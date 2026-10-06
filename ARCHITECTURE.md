# 🏗️ System Design - CI/CD Pipeline

## 1. Diagrama da Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│ Desenvolvedor: git push origin main                     │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│ GitHub (Repositório)                                    │
│ - main branch                                            │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│ GitHub Actions (CI/CD Workflow)                         │
│ - Build com Maven                                        │
│ - Testes unitários                                       │
│ - Build Docker image                                    │
│ - Push para ECR                                          │
│ - Deploy em EC2                                          │
└──────────────────────┬──────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐
    │ Testes   │  │   ECR    │  │  EC2     │
    │ Passam   │  │ Registry │  │ Atualiza │
    └──────────┘  └──────────┘  └────┬─────┘
                                      │
                                      ▼
                            ┌──────────────────────┐
                            │ Docker Container     │
                            │ (App Java + Tomcat)  │
                            └────────┬─────────────┘
                                     │
                                     ▼
                            ┌──────────────────────┐
                            │ AWS RDS (Database)   │
                            │ MySQL 8.0            │
                            └──────────────────────┘
```

---

## 2. Componentes da Arquitetura

### GitHub
- **Função**: Repositório de código
- **Tecnologia**: Git + GitHub Actions
- **Benefício**: Integração nativa com CI/CD

### GitHub Actions (CI/CD)
- **Build**: Maven compila código Java
- **Test**: Testes unitários executados
- **Package**: Docker image criada
- **Push**: Imagem enviada para ECR
- **Deploy**: Script SSH executa em EC2

**Tempo total pipeline**: ~5-10 minutos

### AWS ECR (Elastic Container Registry)
- **Função**: Registro privado de imagens Docker
- **Benefício**: Integração com AWS, sem custo extra
- **Alternativa descartada**: DockerHub (público, requer conta separada)

### AWS EC2
- **Função**: Servidor para rodar container
- **Instância**: t3.micro (free tier)
- **SO**: Ubuntu 22.04 LTS
- **Benefício**: Controle total, fácil de entender, custo baixo

**Por que não usar ECS?**
- Mais complexo para iniciante
- Requer conhecimento de task definitions
- Mais curva de aprendizado
- Válido para produção real com múltiplas instâncias

### AWS RDS (Relational Database Service)
- **Engine**: MySQL 8.0
- **Instância**: db.t3.micro (free tier)
- **Backup**: Automático (backup window)
- **Benefício**: Gerenciado pela AWS, alta disponibilidade

**Por que RDS e não instalar MySQL em EC2?**
- Backups automáticos
- Snapshots para recovery
- Multi-AZ (Alta Disponibilidade)
- Patches automáticos
- Monitoramento CloudWatch

---

## 3. Pipeline CI/CD Detalhado

### Trigger
```
git push origin main
```

### Stage 1: Checkout & Build
```
1. Clone repositório
2. Setup JDK 17
3. Maven: mvn clean package
4. Resultado: minha-app.jar
```

### Stage 2: Docker & ECR
```
1. Build image Docker: docker build -t ...
2. Login ECR com credenciais AWS
3. Push para ECR: docker push ...
4. Tagging: latest + SHA do commit
```

### Stage 3: Deploy
```
1. SSH para EC2 (usando private key do GitHub Secrets)
2. Executar /opt/deploy.sh
3. Deploy script:
   - Pull nova imagem do ECR
   - Stop container antigo
   - Run novo container
   - Health check
```

### Resultado
```
Acesso público: http://<EC2_IP>:8080/actuator/health
```

---

## 4. Decisões de Design

| Aspecto | Escolha | Alternativa | Por quê? |
|--------|---------|------------|---------|
| Orquestração | EC2 Manual | ECS, Lambda | Simplicidade. ECS seria overkill para projeto único |
| Container Registry | ECR | DockerHub | Integração AWS, privado, sem conta extra |
| Database | RDS MySQL | DynamoDB, PostgreSQL | Relacional, familiar, free tier |
| Load Balancing | -none- | ALB | Não necessário com 1 instância |
| Auto Scaling | -none- | ASG | Não necessário com tráfego baixo |
| Secrets | GitHub Secrets | Secrets Manager | Simples, integrado, seguro |
| IaC | Manual/Bash | Terraform | Terraform seria melhor em produção |

---

## 5. Trade-offs

### Escalabilidade
- ✅ Banco: RDS suporta Multi-AZ
- ⚠️ App: Instância única, sem load balancer
- **Improvement**: Adicionar ALB + ASG

### Disponibilidade
- ✅ Database: Multi-AZ automático
- ✅ Deployments: Zero-downtime (health checks)
- ⚠️ Single EC2: Sem redundância
- **Improvement**: Múltiplas EC2 + ASG

### Segurança
- ✅ Credenciais: GitHub Secrets (criptografadas)
- ✅ SSH: Private key em GitHub Secret
- ⚠️ RDS: Público (apenas para teste)
- **Improvement**: RDS em private subnet + bastion host

### Custos
- ✅ EC2 t3.micro: free tier
- ✅ RDS db.t3.micro: free tier
- ✅ ECR: $0.10/GB stored
- **Total**: ~$0 (com free tier)

---

## 6. Monitoramento

### Métricas Importantes

**EC2**
- CPU Utilization
- Memory Usage (via agent customizado)
- Network In/Out

**RDS**
- CPU Utilization
- Database Connections
- Read/Write Latency
- Free Storage Space

**Application**
- Response Time
- Error Rate
- Health Check Status

### Como Monitorar

```bash
# CloudWatch (AWS Console)
# EC2 → Instances → Monitoring

# Logs
docker logs -f minha-app

# Métricas Database
# RDS → Instances → Monitoring
```

---

## 7. Disaster Recovery

### RTO (Recovery Time Objective)
- **EC2 falha**: ~5 minutos (restart + deploy)
- **RDS falha**: ~1 minuto (failover automático se Multi-AZ)

### RPO (Recovery Point Objective)
- **Database**: Último snapshot (automático diariamente)

### Processo de Recovery

```bash
# Se EC2 cair
1. AWS Auto Healing (se configurado)
   OU
2. Manual: Recriar EC2 + executar deploy script
3. Tempo: ~5-10 minutos

# Se RDS cair
1. Failover automático (Multi-AZ)
   OU
2. Manual: Restore de snapshot
3. Tempo: ~1-5 minutos
```

---

## 8. Melhorias para Produção Real

### Curto Prazo (1-2 sprints)
- [ ] ALB para load balancing
- [ ] ASG (Auto Scaling Group)
- [ ] RDS em private subnet
- [ ] Bastion host para acesso
- [ ] CloudWatch dashboards
- [ ] Alerts configurados

### Médio Prazo (1-2 meses)
- [ ] Terraform para IaC
- [ ] ECS em vez de EC2 manual
- [ ] RDS Multi-AZ
- [ ] VPN para acesso ao banco
- [ ] Log aggregation (CloudWatch Logs)

### Longo Prazo (3+ meses)
- [ ] Kubernetes (EKS)
- [ ] Service Mesh (Istio)
- [ ] CI/CD avançado (GitOps)
- [ ] Disaster Recovery automático
- [ ] Multi-region

---

## 9. Stack Tecnológico

```
Linguagem & Framework:
├─ Java 17
├─ Spring Boot 3.x
├─ Maven
└─ JPA/Hibernate

Containerização:
├─ Docker
├─ Docker Compose (local)
└─ ECR (registry)

CI/CD:
├─ GitHub Actions
├─ Bash Scripts
└─ SSH Deploy

Cloud Infrastructure:
├─ AWS EC2 (Compute)
├─ AWS ECR (Container Registry)
├─ AWS RDS (Database)
├─ AWS IAM (Access Control)
└─ AWS CloudWatch (Monitoring)

Database:
├─ MySQL 8.0
├─ Flyway (Migrations)
└─ JDBC Driver

Ferramentas:
├─ Git + GitHub
├─ JDK 17
├─ Maven 3.9+
└─ Docker Engine
```

---

## 10. Sequência de Implementação

### Fase 1: Fundação (Dia 1)
```
GitHub Repo
    ↓
AWS Setup (IAM, VPC)
    ↓
RDS Database
    ↓
Aplicação Local + Docker
```

### Fase 2: CI/CD (Dia 2)
```
ECR Registry
    ↓
GitHub Actions (Build)
    ↓
EC2 Instance
    ↓
GitHub Actions (Deploy)
```

### Fase 3: Robustez (Dia 3)
```
Flyway Migrations
    ↓
Health Checks
    ↓
Secrets Management
    ↓
Documentação
```

---

## 11. Perguntas Frequentes & Respostas

### P: Por que não usou Kubernetes?
**R:** Kubernetes seria overhead para projeto único. EC2 + Docker é mais simples e suficiente. Em produção com múltiplos serviços, Kubernetes seria melhor. Neste caso, queremos aprender CI/CD primeiro.

### P: Como você lidaria com database migrations?
**R:** Usando Flyway. Scripts SQL no formato `V1__schema.sql` são executados automaticamente no startup da aplicação. Versionamento garante que não rodem 2x.

### P: E se o deploy falhar no meio?
**R:** O script tem health checks. Se container não responder em 30s, o deploy falha e mantém versão anterior rodando. Zero-downtime deployment.

### P: Quanto tempo leva um deploy?
**R:** ~5-10 minutos total:
- Build Maven: ~2 min
- Docker build: ~2 min
- Push ECR: ~30s
- Deploy & health check: ~2 min

### P: Como você monitoraria isso em produção?
**R:** CloudWatch dashboards para:
- CPU/Memory do EC2
- Conexões ativas do RDS
- Taxa de erro da aplicação
- Health check status
- Alertas se algo cai

### P: E se a imagem Docker for muito grande?
**R:** Usar multi-stage builds (já implementado no Dockerfile) e cache de layers do Docker para acelerar builds.

### P: Como fazer rollback se algo der errado?
**R:** Docker tags guardam histórico. Basta fazer deploy de versão anterior:
```bash
bash /opt/deploy.sh <ECR_REGISTRY> <OLD_SHA>
```

### P: RDS em produção precisa ser Multi-AZ?
**R:** Sim, para alta disponibilidade. Neste projeto é single-AZ (free tier), mas em produção seria Multi-AZ.

### P: Por que não usar Secrets Manager em vez de GitHub Secrets?
**R:** GitHub Secrets é mais simples para iniciante. Secrets Manager seria para múltiplos serviços/aplicações. Trade-off: simplicidade vs. escalabilidade.

### P: Como escalar a aplicação?
**R:** Fase 1 (agora): Single EC2
Fase 2: ALB + ASG (múltiplas EC2, escalamento automático)
Fase 3: ECS/Kubernetes (orquestração)

---

## 12. Referências & Links

### Documentação Oficial
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [AWS EC2 Docs](https://docs.aws.amazon.com/ec2/)
- [AWS ECR Docs](https://docs.aws.amazon.com/ecr/)
- [AWS RDS Docs](https://docs.aws.amazon.com/rds/)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Docker Docs](https://docs.docker.com/)
- [Flyway Docs](https://flywaydb.org/)
- [Maven Docs](https://maven.apache.org/)

### Tutoriais Úteis
- [GitHub Actions for Java](https://github.com/actions/setup-java)
- [Docker Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Spring Boot with RDS](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.sql)
- [AWS Free Tier](https://aws.amazon.com/free/)

### Best Practices
- [The Twelve-Factor App](https://12factor.net/)
- [Spring Boot Best Practices](https://spring.io/guides)
- [Docker Security](https://docs.docker.com/engine/security/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)

---

## 13. Checklist de Implementação

### Dia 1 ✅
- [ ] AWS Account criada
- [ ] IAM User com permissões
- [ ] RDS instância criada e testada
- [ ] Aplicação SpringBoot conectando ao RDS
- [ ] Dockerfile criado e testado localmente
- [ ] Código commitado no GitHub

### Dia 2 ✅
- [ ] ECR repository criado
- [ ] GitHub Secrets configurados
- [ ] Workflow build implementado e testando
- [ ] EC2 instância criada
- [ ] Docker instalado em EC2
- [ ] Deploy script criado
- [ ] Workflow deploy implementado e funcionando

### Dia 3 ✅
- [ ] Flyway migrations implementadas
- [ ] Health checks robustos
- [ ] Secrets management configurado
- [ ] Testes de deploy realizados
- [ ] ARCHITECTURE.md documentado
- [ ] Roteiro de entrevista preparado

---

## 14. Próximas Etapas (Pós-Projeto)

### Semana 2-4
- [ ] Adicionar ALB (Application Load Balancer)
- [ ] Configurar ASG (Auto Scaling Group)
- [ ] Implementar CloudWatch dashboards
- [ ] Adicionar alerts

### Mês 2-3
- [ ] Migrar para Terraform (Infrastructure as Code)
- [ ] Implementar ECS
- [ ] RDS Multi-AZ
- [ ] Bastion host para acesso seguro

### Mês 4+
- [ ] Explorar Kubernetes (EKS)
- [ ] GitOps (ArgoCD)
- [ ] Disaster Recovery automático
- [ ] Multi-region

---

**Versão**: 1.0  
**Data**: Outubro 2026  
**Autor**: Seu Nome  
**Status**: Em Implementação (Projeto 3 Dias)

---

*Este documento é vivo e deve ser atualizado conforme o projeto evolui e novas decisões são tomadas.*
