{{- define "ironforge-microservice.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "ironforge-microservice.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{- define "ironforge-microservice.labels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
app.kubernetes.io/name: {{ include "ironforge-microservice.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/part-of: ironforge
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "ironforge-microservice.selectorLabels" -}}
app.kubernetes.io/name: {{ include "ironforge-microservice.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}