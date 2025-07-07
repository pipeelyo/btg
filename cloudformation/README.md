# Guía de Despliegue de la Aplicación BTG

Esta guía explica cómo desplegar la aplicación Spring Boot BTG en AWS Elastic Beanstalk utilizando AWS CloudFormation.

## Requisitos Previos

1. Cuenta de AWS con los permisos necesarios
2. AWS CLI instalado y configurado
3. Maven instalado (para construir la aplicación)
4. Un par de claves EC2 en tu cuenta de AWS
5. Un bucket S3 para almacenar el paquete de la aplicación

## Pasos de Despliegue

### 1. Construir la Aplicación

Antes de desplegar, construye el archivo JAR de la aplicación:

```bash
mvn clean package
```

El archivo JAR compilado estará disponible en `target/btg-0.0.1-SNAPSHOT.jar`

### 2. Empaquetar la Aplicación

Crea un archivo ZIP que contenga el JAR de la aplicación y los archivos de configuración necesarios:

```bash
mkdir -p .ebextensions
zip -r btg-application.zip .ebextensions/ target/btg-*.jar
```

### 3. Subir la Aplicación a S3

Sube el archivo ZIP a un bucket S3 (reemplaza `nombre-tu-bucket` con el nombre de tu bucket):

```bash
aws s3 cp btg-application.zip s3://nombre-tu-bucket/btg-application.zip
```

### 4. Configurar los Parámetros de Despliegue

Edita el archivo `parameters-simplified.json` con los valores adecuados para tu entorno:

```json
[
  {
    "ParameterKey": "EnvironmentName",
    "ParameterValue": "dev"
  },
  {
    "ParameterKey": "ApplicationName",
    "ParameterValue": "btg-app"
  },
  {
    "ParameterKey": "InstanceType",
    "ParameterValue": "t3.micro"
  },
  {
    "ParameterKey": "KeyName",
    "ParameterValue": "NombreDeTuParDeClaves"
  },
  {
    "ParameterKey": "S3Bucket",
    "ParameterValue": "nombre-tu-bucket"
  },
  {
    "ParameterKey": "S3Key",
    "ParameterValue": "btg-application.zip"
  }
]
```

### 5. Desplegar la Pila de CloudFormation

Usa la CLI de AWS para desplegar la pila de CloudFormation:

```bash
aws cloudformation create-stack \
  --stack-name btg-application-stack \
  --template-body file://cloudformation/template-simplified.yaml \
  --parameters file://cloudformation/parameters-simplified.json \
  --capabilities CAPABILITY_IAM \
  --region us-east-2
```

## Monitoreo del Despliegue

### Verificar el Estado de la Pila

```bash
aws cloudformation describe-stacks --stack-name btg-application-stack --region us-east-2 --query 'Stacks[0].StackStatus' --output text
```

### Verificar el Estado del Entorno Elastic Beanstalk

```bash
aws elasticbeanstalk describe-environments --region us-east-2 --query 'Environments[].{Nombre:EnvironmentName, Estado:Status, Salud:Health, URL:CNAME}' --output table
```

### Ver los Logs de la Aplicación

```bash
# Obtener el nombre del entorno
NOMBRE_ENTORNO=$(aws elasticbeanstalk describe-environments --region us-east-2 --query 'Environments[0].EnvironmentName' --output text)

# Ver logs
aws elasticbeanstalk retrieve-environment-info --environment-name $NOMBRE_ENTORNO --info-type tail --region us-east-2
```

## Solución de Problemas Comunes

### La pila falla al crear recursos

1. Verifica que tu cuenta de AWS esté completamente verificada y habilitada.
2. Asegúrate de que no hayas alcanzado los límites de servicio para EC2, Elastic Beanstalk o VPC.
3. Verifica que el par de claves EC2 especificado exista en la región correcta.
4. Revisa los eventos de CloudFormation para identificar el recurso que está fallando:

```bash
aws cloudformation describe-stack-events --stack-name btg-application-stack --region us-east-2 --query 'StackEvents[?contains(ResourceStatus,`FAILED`)].{Tipo:ResourceType, Estado:ResourceStatus, Razon:ResourceStatusReason}' --output table
```

### La aplicación no se inicia correctamente

1. Verifica los logs de la aplicación usando los comandos de monitoreo anteriores.
2. Asegúrate de que la aplicación esté configurada para escuchar en el puerto 5000 (puerto predeterminado para Elastic Beanstalk).
3. Verifica que las variables de entorno estén configuradas correctamente en el entorno de Elastic Beanstalk.

## Limpieza

Para eliminar todos los recursos creados:

```bash
# Eliminar el entorno de Elastic Beanstalk
aws elasticbeanstalk terminate-environment --environment-name dev-btg-app --region us-east-2

# Eliminar la pila de CloudFormation
aws cloudformation delete-stack --stack-name btg-application-stack --region us-east-2

# Eliminar el archivo de la aplicación de S3 (opcional)
aws s3 rm s3://nombre-tu-bucket/btg-application.zip
```
## Configuración de MongoDB Atlas

La aplicación está configurada para conectarse a MongoDB Atlas. Asegúrate de que:

1. Tienes un clúster configurado en MongoDB Atlas.
2. La dirección IP de los servidores de Elastic Beanstalk está en la lista blanca de MongoDB Atlas.
3. La cadena de conexión de MongoDB está configurada correctamente en las variables de entorno de Elastic Beanstalk.

Para configurar la cadena de conexión en Elastic Beanstalk:

1. Ve a la consola de AWS > Elastic Beanstalk
2. Selecciona tu entorno
3. Ve a "Configuración" > "Software"
4. Desplázate hasta "Variables de entorno"
5. Agrega una variable llamada `MONGODB_URI` con tu cadena de conexión de MongoDB Atlas

## Soporte

Si encuentras problemas durante el despliegue, por favor verifica la documentación oficial de AWS o abre un caso de soporte en la consola de AWS.
    ParameterKey=ApplicationName,ParameterValue=btg-application \
    ParameterKey=InstanceType,ParameterValue=t3.micro \
    ParameterKey=DBName,ParameterValue=btgdb \
    ParameterKey=DBUsername,ParameterValue=dbadmin \
    ParameterKey=DBPassword,ParameterValue=YourSecurePassword123! \
    ParameterKey=DBInstanceClass,ParameterValue=db.t3.micro \
    ParameterKey=KeyName,ParameterValue=your-key-pair-name \
  --capabilities CAPABILITY_IAM
```

### 5. Monitor the Deployment

Monitor the stack creation progress in the AWS CloudFormation console or using the CLI:

```bash
aws cloudformation describe-stacks --stack-name btg-application-stack --query 'Stacks[0].StackStatus'
```

### 6. Access the Application

Once the stack is created, you can find the application URL in the CloudFormation outputs or by running:

```bash
aws cloudformation describe-stacks --stack-name btg-application-stack --query 'Stacks[0].Outputs[?OutputKey==`WebsiteURL`].OutputValue' --output text
```

## Updating the Stack

To update the stack with changes:

1. Update your application code and rebuild the JAR
2. Create a new ZIP file and upload it to S3
3. Update the stack with the new application version:

```bash
aws cloudformation update-stack \
  --stack-name btg-application-stack \
  --use-previous-template \
  --parameters \
    ParameterKey=EnvironmentName,UsePreviousValue=true \
    ParameterKey=ApplicationName,UsePreviousValue=true \
    ParameterKey=InstanceType,UsePreviousValue=true \
    ParameterKey=DBName,UsePreviousValue=true \
    ParameterKey=DBUsername,UsePreviousValue=true \
    ParameterKey=DBPassword,UsePreviousValue=true \
    ParameterKey=DBInstanceClass,UsePreviousValue=true \
    ParameterKey=KeyName,UsePreviousValue=true
```

## Cleaning Up

To delete the stack and all its resources:

```bash
aws cloudformation delete-stack --stack-name btg-application-stack
```

## Architecture

The CloudFormation template creates the following resources:

- **Elastic Beanstalk Application**: Manages the application deployment
- **EC2 Instances**: Runs the Spring Boot application
- **RDS Database**: PostgreSQL database for the application
- **Security Groups**: Controls network access to the application and database
- **IAM Roles**: Defines permissions for the EC2 instances

## Best Practices

1. **Security**:
   - Use AWS Secrets Manager or Parameter Store for sensitive information
   - Enable encryption at rest and in transit
   - Apply the principle of least privilege to IAM roles

2. **High Availability**:
   - Enable Multi-AZ deployment for production environments
   - Configure appropriate Auto Scaling settings
   - Use a load balancer for distributing traffic

3. **Monitoring**:
   - Enable CloudWatch Logs for application and system logs
   - Set up CloudWatch Alarms for monitoring application health
   - Use AWS X-Ray for distributed tracing

## Troubleshooting

- Check the Elastic Beanstalk environment logs for application errors
- Verify that the security groups allow the necessary traffic
- Ensure the database is accessible from the application instances
- Check CloudFormation events for any deployment issues

## Support

For any issues or questions, please contact the development team.
