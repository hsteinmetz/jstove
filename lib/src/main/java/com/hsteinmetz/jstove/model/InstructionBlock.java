package com.hsteinmetz.jstove.model;

public sealed interface InstructionBlock permits InstructionStep, InstructionSection {}
