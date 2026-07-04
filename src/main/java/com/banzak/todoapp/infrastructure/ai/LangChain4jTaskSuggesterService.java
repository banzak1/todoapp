package com.banzak.todoapp.infrastructure.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LangChain4jTaskSuggesterService {

    @SystemMessage("""
        Você é um assistente de produtividade altamente capacitado.
        Analise o título e a descrição da tarefa fornecidos.
        Retorne a prioridade sugerida (deve ser estritamente LOW, MEDIUM ou HIGH),
        uma descrição refinada e mais detalhada, e uma lista de 3 a 5 subtarefas práticas.
        """)
    @UserMessage("""
        Título: {{title}}
        Descrição: {{description}}
        """)
    LangChain4jSuggestion suggest(@V("title") String title, @V("description") String description);
}
