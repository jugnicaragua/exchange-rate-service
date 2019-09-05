#!/bin/bash

# Source:
# https://medium.com/@beld_pro/quick-tip-creating-a-postgresql-container-with-default-user-and-password-8bb2adb82342

# Immediately exits if any error occurs during the script
# execution. If not set, an error could occur and the
# script would continue its execution.
set -o errexit


# Creating an array that defines the environment variables
# that must be set. This can be consumed later via arrray
# variable expansion ${REQUIRED_ENV_VARS[@]}.
readonly REQUIRED_ENV_VARS=(
  "EXCHANGE_RATE_DB_USER"
  "EXCHANGE_RATE_DB_PASSWORD"
  "EXCHANGE_RATE_DB_DATABASE")


# Main execution:
# - verifies if all environment variables are set
# - runs the SQL code to create user and database
main() {
  check_env_vars_set
  init_user_and_db
}


# Checks if all of the required environment
# variables are set. If one of them isn't,
# echoes a text explaining which one isn't
# and the name of the ones that need to be
check_env_vars_set() {
  for required_env_var in ${REQUIRED_ENV_VARS[@]}; do
    if [[ -z "${!required_env_var}" ]]; then
      echo "Error:
    Environment variable '$required_env_var' not set.
    Make sure you have the following environment variables set:
      ${REQUIRED_ENV_VARS[@]}
Aborting."
      exit 1
    fi
  done
}


# Performs the initialization in the already-started PostgreSQL
# using the preconfigured POSTGRE_USER user.
init_user_and_db() {
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
     CREATE USER $EXCHANGE_RATE_DB_USER WITH PASSWORD '$EXCHANGE_RATE_DB_PASSWORD';
     CREATE DATABASE $EXCHANGE_RATE_DB_DATABASE;
     GRANT ALL PRIVILEGES ON DATABASE $EXCHANGE_RATE_DB_DATABASE TO $EXCHANGE_RATE_DB_USER;
EOSQL
}

# Executes the main routine with environment variables
# passed through the command line. We don't use them in
# this script but now you know 🤓
main "$@"