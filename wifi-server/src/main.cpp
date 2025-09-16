/**
 * @file main.cpp
 * @author Caglar KOPARIR (ckoparir@gmail.com)
 * @brief
 * Simple home automation system with esp8266
 * and lm35. Project consist of two seperate
 * modules as client and server uses REST API
 * technology.
 *
 * @version 0.1
 * @date 2021-10-18
 *
 * @copyright CTech Copyright (c) 2021
 *
 */
#include "therm_util.h"

void setup(void)
{
  initTerm();
}

void loop(void)
{
  serverLoop();
}