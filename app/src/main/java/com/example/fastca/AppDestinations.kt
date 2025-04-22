object AppDestinations {
    const val SQUARE_GRID_ROUTE = "squareGrid"
    const val SQUARE_DETAIL_ROUTE = "squareDetail"
    const val SQUARE_ID_ARG = "squareId" // Имя аргумента
    val squareDetailRouteWithArg = "$SQUARE_DETAIL_ROUTE/{$SQUARE_ID_ARG}"
}