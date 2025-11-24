@file:Supress("Unused")

package me.silent.parsing

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.element.SyntaxTokenTypes
import com.intellij.platform.syntax.util.lexer.FlexLexer
import org.jetbrains.annotations.ApiStatus
import kotlin.jvm.JvmStatic // Not needed on JVM, but needed when compiling other targets

@ApiStatus.Experimental
%%
%{
  private class State(val state: Int, val lBraceCount: Int, val requiredInterpolationPrefix: Int) {
      override fun toString(): String {
          return "yystate = $state" +
                  (if (lBraceCount == 0) "" else "lBraceCount = $lBraceCount") +
                  (if (requiredInterpolationPrefix == -1) "" else "requiredInterpolationPrefix = $requiredInterpolationPrefix")
      }
  }

  private val states: MutableList<State> = mutableListOf()
  private var lBraceCount = 0
  private var requiredInterpolationPrefix = 0

  private var commentStart = 0
  private var commentDepth = 0

  private fun pushState(state: Int) {
      states.add(State(yystate(), lBraceCount, requiredInterpolationPrefix))
      lBraceCount = 0
      requiredInterpolationPrefix = -1
      yybegin(state)
  }

  private fun pushInterpolationPrefix(interpolationPrefix: Int) {
      states.add(State(yystate(), lBraceCount, requiredInterpolationPrefix))
      lBraceCount = 0
      requiredInterpolationPrefix = interpolationPrefix
      yybegin(STRING_PREFIX)
  }

  private fun popState() {
      val state: State = states.removeLast()
      lBraceCount = state.lBraceCount
      requiredInterpolationPrefix = state.requiredInterpolationPrefix
      yybegin(state.state)
  }

  private fun commentStateToTokenType(state: Int): SyntaxElementType {
    return when (state) {
      BLOCK_COMMENT -> KtGLTokens.BLOCK_COMMENT
      DOC_COMMENT -> KtGLTokens.DOC_COMMENT
      else -> throw IllegalArgumentException("Unexpected state: $state")
    }
  }
%}


%unicode
%class KotlinFlexLexer
%implements FlexLexer
%function advance
%type SyntaxElementType

%xstate STRING_PREFIX STRING RAW_STRING SHORT_TEMPLATE_ENTRY BLOCK_COMMENT DOC_COMMENT
%state LONG_TEMPLATE_ENTRY UNMATCHED_BACKTICK

    DIGIT=[0-9]
    DIGIT_OR_UNDERSCORE = [_0-9]
    DIGITS = {DIGIT} {DIGIT_OR_UNDERSCORE}*
    HEX_DIGIT=[0-9A-Fa-f]
    HEX_DIGIT_OR_UNDERSCORE = [_0-9A-Fa-f]
    WHITE_SPACE_CHAR=[\ \n\t\f]


%%



// (Nested) comments

"/**/" {
    return KtGLTokens.BLOCK_COMMENT
}

"/**" {
            pushState(DOC_COMMENT)
            commentDepth = 0
            commentStart = getTokenStart()
}

"/*" {
            pushState(BLOCK_COMMENT)
            commentDepth = 0
            commentStart = getTokenStart()
}

<BLOCK_COMMENT, DOC_COMMENT> {
    "/*" {
            commentDepth++
    }

    <<EOF>> {
            val state = yystate()
            popState()
            zzStartRead = commentStart
            return commentStateToTokenType(state)
    }

    "*/" {
            if (commentDepth > 0) {
                commentDepth--
            }
            else {
                 val state = yystate()
                 popState()
                 zzStartRead = commentStart
                 return commentStateToTokenType(state)
            }
    }

    [\s\S] {}
}




"output"      { return KtGLTokens.OUTPUT_KEYWORD }
"input"      { return KtGLTokens.INPUT_KEYWORD }
"return"     { return KtGLTokens.RETURN_KEYWORD }
"val"        { return KtGLTokens.VAL_KEYWORD }
"var"        { return KtGLTokens.VAR_KEYWORD }
"fun"        { return KtGLTokens.FUN_MODIFIER }
"as"         { return KtGLTokens.AS_KEYWORD }

// TOKENS

"++"          { return KtGLTokens.PLUSPLUS }
"--"          { return KtGLTokens.MINUSMINUS }
"["          { return KtGLTokens.LBRACKET }
"]"          { return KtGLTokens.RBRACKET }
"{"          { return KtGLTokens.LBRACE }
"}"          { return KtGLTokens.RBRACE }
"("          { return KtGLTokens.LPAR }
")"          { return KtGLTokens.RPAR }
"."          { return KtGLTokens.DOT }
"*"          { return KtGLTokens.MUL }
"+"          { return KtGLTokens.PLUS }
"-"          { return KtGLTokens.MINUS }
"!"          { return KtGLTokens.EXCL }
"/"          { return KtGLTokens.DIV }
"%"          { return KtGLTokens.PERC }

"<"          { return KtGLTokens.LT }
">"          { return KtGLTokens.GT }
"?"          { return KtGLTokens.QUEST }
":"          { return KtGLTokens.COLON }
";;"         { return KtGLTokens.DOUBLE_SEMICOLON}
";"          { return KtGLTokens.SEMICOLON }
"="          { return KtGLTokens.EQ }
","          { return KtGLTokens.COMMA }
"#"          { return KtGLTokens.HASH }
"@"          { return KtGLTokens.AT }

{LONELY_BACKTICK} {
            pushState(UNMATCHED_BACKTICK)
            return SyntaxTokenTypes.BAD_CHARACTER
}

// error fallback
[\s\S]       { return SyntaxTokenTypes.BAD_CHARACTER }
// error fallback for exclusive states
<STRING, RAW_STRING, SHORT_TEMPLATE_ENTRY, BLOCK_COMMENT, DOC_COMMENT> .
             { return SyntaxTokenTypes.BAD_CHARACTER }