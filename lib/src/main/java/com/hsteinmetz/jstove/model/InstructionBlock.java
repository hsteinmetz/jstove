package com.hsteinmetz.jstove.model;

/**
 * @author Hendrik Steinmetz
 */
public sealed interface InstructionBlock permits InstructionStep, InstructionSection {}
