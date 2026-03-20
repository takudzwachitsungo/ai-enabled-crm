# AI Service (Python)

Dedicated AI layer for the CRM monolith. This service handles summarization and drafting behind a provider abstraction.

## Endpoints
- `GET /health`
- `POST /v1/summarize`
- `POST /v1/draft`

## Local Run
```bash
cd ai-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8090
```

## Docker Run
```bash
docker build -t ai-first-crm-ai-service ./ai-service
docker run --rm -p 8090:8090 --env-file ai-service/.env ai-first-crm-ai-service
```

## Provider Modes
- `AI_PROVIDER=mock` (default): deterministic local responses.
- `AI_PROVIDER=openai`: uses OpenAI API key and model.
