using Microsoft.AspNetCore.Mvc;
using VinheriaAgnelloCRUD.DTOs.Wines;
using VinheriaAgnelloCRUD.Responses;
using VinheriaAgnelloCRUD.Services;

namespace VinheriaAgnelloCRUD.Controllers;

[ApiController]
[Route("api/wines")]
public class WinesController : ControllerBase
{
    private readonly IWineService _wineService;

    public WinesController(IWineService wineService)
    {
        _wineService = wineService;
    }

    /// <summary>
    /// Lista todos os vinhos cadastrados.
    /// </summary>
    /// <returns>Lista de vinhos ordenada por nome.</returns>
    [HttpGet]
    [ProducesResponseType(typeof(ApiResponse<IReadOnlyCollection<WineResponse>>), StatusCodes.Status200OK)]
    public async Task<ActionResult<ApiResponse<IReadOnlyCollection<WineResponse>>>> GetAll()
    {
        var wines = await _wineService.GetAllAsync();

        return Ok(ApiResponse<IReadOnlyCollection<WineResponse>>.Ok(
            wines,
            "Vinhos encontrados com sucesso."));
    }

    /// <summary>
    /// Busca um vinho pelo identificador.
    /// </summary>
    /// <param name="id">Identificador do vinho.</param>
    /// <returns>Vinho encontrado.</returns>
    [HttpGet("{id:guid}")]
    [ProducesResponseType(typeof(ApiResponse<WineResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<WineResponse>), StatusCodes.Status404NotFound)]
    public async Task<ActionResult<ApiResponse<WineResponse>>> GetById(Guid id)
    {
        var wine = await _wineService.GetByIdAsync(id);

        if (wine is null)
            return NotFound(ApiResponse<WineResponse>.Fail("Vinho nao encontrado."));

        return Ok(ApiResponse<WineResponse>.Ok(
            wine,
            "Vinho encontrado com sucesso."));
    }

    /// <summary>
    /// Cadastra um novo vinho.
    /// </summary>
    /// <param name="request">Dados do vinho.</param>
    /// <returns>Vinho cadastrado.</returns>
    [HttpPost]
    [ProducesResponseType(typeof(ApiResponse<WineResponse>), StatusCodes.Status201Created)]
    [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<ApiResponse<WineResponse>>> Create(CreateWineRequest request)
    {
        var wine = await _wineService.CreateAsync(request);

        return CreatedAtAction(
            nameof(GetById),
            new { id = wine.Id },
            ApiResponse<WineResponse>.Ok(wine, "Vinho cadastrado com sucesso."));
    }

    /// <summary>
    /// Atualiza os dados de um vinho existente.
    /// </summary>
    /// <param name="id">Identificador do vinho.</param>
    /// <param name="request">Novos dados do vinho.</param>
    /// <returns>Vinho atualizado.</returns>
    [HttpPut("{id:guid}")]
    [ProducesResponseType(typeof(ApiResponse<WineResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<WineResponse>), StatusCodes.Status404NotFound)]
    [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<ApiResponse<WineResponse>>> Update(Guid id, UpdateWineRequest request)
    {
        var wine = await _wineService.UpdateAsync(id, request);

        if (wine is null)
            return NotFound(ApiResponse<WineResponse>.Fail("Vinho nao encontrado."));

        return Ok(ApiResponse<WineResponse>.Ok(
            wine,
            "Vinho atualizado com sucesso."));
    }

    /// <summary>
    /// Remove um vinho pelo identificador.
    /// </summary>
    /// <param name="id">Identificador do vinho.</param>
    /// <returns>Resultado da remocao.</returns>
    [HttpDelete("{id:guid}")]
    [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
    public async Task<ActionResult<ApiResponse<object>>> Delete(Guid id)
    {
        var deleted = await _wineService.DeleteAsync(id);

        if (!deleted)
            return NotFound(ApiResponse<object>.Fail("Vinho nao encontrado."));

        return Ok(ApiResponse<object>.Ok(
            null!,
            "Vinho removido com sucesso."));
    }
}
