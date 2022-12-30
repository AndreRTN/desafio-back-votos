
## Requisitos:

- Docker (Se não possuir o docker precisará baixar as dependências contidas nos arquivos compose.yml e configurar manualmente)

- JDK 17
- Maven (ou pode baixar o .jar diretamente)
- Microservice da validação do CPF e dos resultados (o projeto funciona sem ele , mas não irá enviar a mensagem para o kafka
e nem será possível validar o cpf) https://github.com/AndreRTN/votos-microservice

## Para iniciar o backend responsável pela votação

Passos:

Vá até a pasta do projeto e entre na pasta do docker ./docker

No terminal, digite: "docker-compose -p backend-votos up -d"

Irá iniciar o postgres e o cliente pgAdmin, para acessar o cliente a url é: http://localhost:15432

o nome de usuário é postgres e a senha é:backendvotos123

Com o banco de dados postgres já rodando, Na pasta do projeto, digite mvn package, irá gerar um .jar na pasta target,
"java -jar .\target\desafio-back-votos-0.0.1-SNAPSHOT.jar" para executar o backend dos votos no localhost:8080
("Necessário ter o banco postgres na porta 5432 em execução")

Para facilitar deixarei um link dos .jar das duas aplicações, se preferir baixar diretamente só precisará fazer o passo do docker
- Aplicação dos votos: https://drive.google.com/file/d/1dpVVX0FaH7uklWgA23gMP1A-oZoWcCka/view?usp=share_link
- Microservice: https://drive.google.com/file/d/1BL6gV_M-HBz3oRnjxYpoqMmtFX1WtCJH/view?usp=share_link

## Desafio-back-votos
 Desafio Técnico

https://github.com/rh-southsystem/desafio-back-votos

- [x] Cadastrar uma nova pauta;
- [x] Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por um tempo determinado na chamada de abertura ou 1 minuto por default);
- [x] Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado é identificado por um id único e pode votar apenas uma vez por pauta);
- [x] Contabilizar os votos e dar o resultado da votação na pauta.

Tarefa Bônus 1 - Integração com sistemas externos
Integrar com um sistema que verifique, a partir do CPF do associado, se ele pode votar

GET https://user-info.herokuapp.com/associates/{cpf}
- [x] Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found). Você pode usar geradores de CPF para gerar CPFs válidos;
- [x] Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a operação Exemplos de retorno do serviço

Tarefa Bônus 2 - Mensageria e filas
- [x] Classificação da informação: Uso Interno O resultado da votação precisa ser informado para o restante da plataforma, isso deve ser feito preferencialmente através de mensageria. Quando a sessão de votação fechar, poste uma mensagem com o resultado da votação.

Tarefa Bônus 3 - Performance

Realizei alguns testes de perfomance usando o JMeter, nos testes de resultado eu usei 100 mil o número de requests,
já no de cadastro da pauta e de votação utilizei 10 mil.

- Primeiro teste - Resultado da votação na pauta
  - Como após a contagem dos votos o resultado da pauta não irá mudar, tomei a decisão de usar cache nesse endpoint, 
  fazendo com que ele consiga atender a 3301 solicitações por segundo, o que daria 198060 por minuto.


- Segundo teste - Cadastrar Pauta
  -  Como aqui é necessário fazer chamada ao banco de dados, o número de solicitações atendidas por segundo é menor que
  o teste anterior, 209 por segundo, o que daria 12540 por minuto


- Terceiro teste - Votação
  -  Por ter uma validação maior: usuário pode votar apenas uma vez por pauta, verificar se a sessão de votação está aberta ou se a pauta já encerrou
  - esse é o método que mais leva tempo para atender as solicitações, 70.5 por segundo, 4230 por minuto
  - para contornar esse problema , vejo várias formas:
    - Usar réplicas de bancos de leitura e de escrita para maior escalabilidade
    - Cluster de banco de dados
    - Se o tempo de resposta for muito importante para o usuário, utilizar um sistema de filas e mensageria para ir adicionando
    os votos e salvar quando o sistema conseguir processar
    - Processar votos em lotes, em vez de abrir uma conexão com o banco para cada requisição feita, processar em um range, como de 1000 em 1000 votos, a cada X segundos


# Explicação sobre algumas decisões tomadas:
  - Decidi utilizar o Webflux em vez do Spring MVC Tradicional devido ao paradigma reativo e por fazer melhor uso das threads
  abertas por requests, fonte: https://www.baeldung.com/spring-mvc-async-vs-webflux
  - Criei uma aplicação a mais  que consome os resultados da votação da pauta do kafka para isolar as responsabilidades, 
  já que além de outros serviços e aplicações também conseguirem enviar a mensagem para esse consumidor, se alguma das aplicações sofrer com gargalo, a outra não é afetada.
  - Como não encontrei nenhuma API gratuita e de facil acesso para checar se o  CPF Disponível pode votar, criei um microsserviço que verifica um cpf e retorna se é valido.
  - Utilizei o cache default do spring para evitar over engineering, mas acredito que algo como o Redis encaixaria melhor para o propósito da aplicação.
  - Para checar se a votação da pauta já foi encerrada, utilizei um job que roda a cada 1 minuto, pegando todos as pautas que estão em andamento
  e que ainda não expiraram para reduzir a quantidade de chamadas ao banco, existem outras formas de fazer isso, como o tempo do job é de 1 minuto pode acontecer de os
  dados não estarem sincronizados, então no momento da votação eu valido se já encerrou também, e quando o job rodar novamente irá atualizar a tabela no banco de dados.

