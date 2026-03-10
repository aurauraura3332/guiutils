# GUI Utils — Minecraft 1.8.9 Forge Mod

Inspirado no ui-utils do CoderX. Funcionalidades implementadas:

---

## Funcionalidades

| Botão / Tecla | O que faz |
|---|---|
| **Close without packet** | Fecha o GUI apenas no lado do cliente. Descarta todos os pacotes na fila. O servidor continua achando que o GUI está aberto. |
| **Save GUI** | Salva o GUI atual. Use junto com "Close without packet" para fechar localmente e depois reabrir com **V**. |
| **Delay packets: true/false** | Quando ativado, todos os pacotes de saída são enfileirados. Ao desativar, são todos enviados de uma vez. O número entre parênteses mostra quantos estão na fila. |
| **Barra de comando** (caixa de texto) | Permite digitar e executar comandos enquanto o GUI está aberto. |
| **V** (keybind) | Reabre o GUI salvo (quando não há nenhum GUI aberto). |
| **O** (keybind) | Envolve o GUI atualmente aberto com o overlay do GUI Utils. |

---

## Como usar 

1. Abra um baú / inventário no servidor.
2. Pressione **O** para ativar o overlay.
3. Clique em **Save GUI** para salvar o estado.
4. Ative **Delay packets** para congelar os pacotes de saída.
5. Mexa no inventário como quiser.
6. Clique **Close without packet** → o GUI fecha localmente, o servidor ainda o vê aberto.
7. Pressione **V** a qualquer momento para reabrir o GUI salvo.
8. Para enviar tudo de uma vez: reabra com V e desative o Delay (ou mantenha ativo e manipule à vontade).

---

## Notas técnicas

- O **Mixin** em `MixinNetworkManager` intercepta `NetworkManager#sendPacket` via `@Inject` com `cancellable = true`. Quando `delayPackets = true`, o pacote é adicionado à fila e o envio original é cancelado.
- O `flushQueue()` itera a fila e chama `networkManager.sendPacket()` diretamente para cada pacote.
- O `dropQueue()` apenas limpa a lista sem enviar nada — isso é o que "Close without packet" usa.
- A restauração do GUI via **V** só funciona quando nenhum outro GUI está aberto (`mc.currentScreen == null`).
